package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.ModSettings;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.ModSettingsEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@AllArgsConstructor
public class ModSettingsService
{
    private final ModSettingsStorage modSettingsStorage;

    public Mono<String> getModSettingsContent(long id)
    {
        return this.modSettingsStorage.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Could not find settings for id = " + id)))
                .flatMap(entity -> modSettingsStorage.readModSettingsFileContent(entity.getName(), entity.isActive()));
    }

    public Mono<Void> saveModSettings(ModSettings modSettings)
    {
        if (modSettings.getName().contains("."))
            throw new IllegalArgumentException("Mod settings name cannot contain '.'");

        Mono<Void> disableAll;
        if (modSettings.isActive())
        {
            disableAll = modSettingsStorage.findActive()
                            .filter(modSettingsEntity -> !modSettingsEntity.getId().equals(modSettings.getId()))
                            .map(modSettingsEntity -> modSettingsStorage.deactivateModSettings(modSettingsEntity.getId(), modSettingsEntity.getName()))
                            .then(modSettingsStorage.disableAll());
        }
        else
        {
            disableAll = modSettingsStorage.findActive()
                    .filter(modSettingsEntity -> modSettingsEntity.getId().equals(modSettings.getId()))
                    .map(modSettingsEntity -> modSettingsStorage.deactivateModSettings(modSettingsEntity.getId(), modSettingsEntity.getName()))
                    .map(t -> modSettingsStorage.deleteModSettingsFile("default", true))
                    .then();
        }

        return disableAll
                .then(Mono.just(modSettings))
                .map(this::toEntity)
                .flatMap(modSettingsStorage::save)
                .then();
    }

    public Mono<Void> saveModSettingsContent(long id, String content)
    {
        return this.modSettingsStorage.findById(id)
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

    public Mono<ModSettings> getModSettingsWithoutContents(long id)
    {
        return this.modSettingsStorage.findById(id)
                .map(this::toDomain);
    }

    public Flux<ModSettings> getModSettingsWithoutContents()
    {
        return this.modSettingsStorage.findAll()
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
        return this.modSettingsStorage.findById(id)
                .flatMap(entity -> this.modSettingsStorage.deleteModSettingsFile(entity.getName(), entity.isActive())
                        .then(Mono.just(entity)))
                .flatMap(modSettingsStorage::delete);
    }
}
