package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.ModSettings;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.ModSettingsEntity;
import pl.bartlomiejstepien.armaserverwebgui.repository.ModSettingsRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@AllArgsConstructor
public class ModSettingsService
{
    private final ModSettingsStorage modSettingsStorage;
    private final ModSettingsRepository modSettingsRepository;

    public Mono<String> getModSettingsContent(String name)
    {
        return this.modSettingsRepository.findByName(name)
                .map(ModSettingsEntity::isActive)
                .flatMap(active -> modSettingsStorage.readModSettingsFileContent(name, active));
    }

    public Mono<String> getModSettingsContent(long id)
    {
        return this.modSettingsRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Could not find settings for id = " + id)))
                .flatMap(entity -> modSettingsStorage.readModSettingsFileContent(entity.getName(), entity.isActive()));
    }

    public Mono<Void> saveModSettings(ModSettings modSettings)
    {
        Mono<Void> disableAll = Mono.empty();
        if (modSettings.isActive())
        {
            disableAll = modSettingsRepository.disableAll();
        }

        return disableAll
                .then(Mono.just(modSettings))
                .map(this::toEntity)
                .flatMap(modSettingsRepository::save)
                .then();
    }

    public Mono<Void> saveModSettingsContent(long id, String content)
    {
        return this.modSettingsRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Could not find settings for id = " + id)))
                .flatMap(entity -> modSettingsStorage.saveModSettingsFileContent(entity.getName(), entity.isActive(), content));
    }

    private ModSettingsEntity toEntity(ModSettings modSettings)
    {
        return ModSettingsEntity.builder()
                .id(modSettings.getId())
                .name(modSettings.getName())
                .active(modSettings.isActive())
                .build();
    }

    public Flux<ModSettings> getModSettingsWithoutContents()
    {
        return this.modSettingsRepository.findAll()
                .map(this::toDomain);
    }

    private ModSettings toDomain(ModSettingsEntity entity)
    {
        return ModSettings.builder()
                .id(entity.getId())
                .name(entity.getName())
                .active(entity.isActive())
                .build();
    }

    public Mono<Void> deleteModSettings(long id)
    {
        return this.modSettingsRepository.findById(id)
                .flatMap(entity -> this.modSettingsStorage.deleteModSettingsFile(entity.getName(), entity.isActive())
                        .then(Mono.just(entity)))
                .flatMap(modSettingsRepository::delete);
    }
}
