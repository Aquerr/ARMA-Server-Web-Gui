package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.ModSettings;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.ModSettingsHeader;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.ModSettingsEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.String.format;

@Service
@Slf4j
@AllArgsConstructor
public class ModSettingsService
{
    private static final String[] DISALLOWED_MOD_SETTINGS_NAME_CHARACTERS =
            {" ", "\\", "/", ":", "*", "?", "\"", "<", ">", "|", "."};

    private final ModSettingsStorage modSettingsStorage;

    public Mono<String> getModSettingsContent(long id)
    {
        return this.modSettingsStorage.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Could not find settings for id = " + id)))
                .flatMap(entity -> modSettingsStorage.readModSettingsFileContent(entity.getName(), entity.isActive()));
    }

    public Mono<ModSettingsHeader> saveModSettings(ModSettings modSettings)
    {
        validateModSettingsName(modSettings);

        ModSettingsEntity entity = toEntity(modSettings);

        Mono<?> mono = Mono.empty();
        if (modSettings.isActive())
        {
            mono = mono.then(modSettingsStorage.findActive())
                    .zipWhen(modSettingsEntity -> modSettingsStorage.readModSettingsFileContent(modSettingsEntity.getName(), true))
                    .flatMap(settingsWithContent -> {
                        String name = settingsWithContent.getT1().getName();
                        if (!settingsWithContent.getT1().getId().equals(modSettings.getId()))
                        {
                            return modSettingsStorage.saveModSettingsFileContent(name, false, settingsWithContent.getT2());
                        }
                        else return Mono.empty();
                    })
                    .then(Mono.just(modSettings))
                    .filter(settings -> Objects.nonNull(settings.getId()))
                    .flatMap(settings -> modSettingsStorage.findById(settings.getId()))
                    .flatMap(modSettingsEntity -> modSettingsStorage.deleteModSettingsFile(modSettingsEntity.getName(), modSettingsEntity.isActive()))
                    .then(modSettingsStorage.deactivateAll());
        }
        else
        {
            mono = Mono.just(modSettings)
                    .filter(settings -> Objects.nonNull(settings.getId()))
                    .flatMap(settings -> modSettingsStorage.findById(settings.getId()))
                    .flatMap(settings -> modSettingsStorage.deleteModSettingsFile(settings.getName(), settings.isActive()));
        }

        return mono
                .then(Mono.defer(() -> this.modSettingsStorage.saveModSettingsFileContent(modSettings.getName(), modSettings.isActive(), modSettings.getContent())))
                .then(this.modSettingsStorage.save(entity))
                .map(ModSettingsService::toDomain);
    }

    private static void validateModSettingsName(ModSettings modSettings)
    {
        Stream<String> charactersStream = Arrays.stream(DISALLOWED_MOD_SETTINGS_NAME_CHARACTERS);
        String illegalCharacter = charactersStream.filter(character -> modSettings.getName().contains(character))
                .findFirst()
                .orElse(null);

        if (illegalCharacter == null)
            return;

        throw new IllegalArgumentException(format("Mod settings name '%s' contains illegal character '%s'.", modSettings.getName(), illegalCharacter));
    }

    private static ModSettingsEntity toEntity(ModSettingsHeader modSettingsHeader)
    {
        return ModSettingsEntity.builder()
                .id(modSettingsHeader.getId())
                .name(modSettingsHeader.getName())
                .active(modSettingsHeader.isActive())
                .build();
    }

    public Mono<ModSettingsHeader> getModSettingsWithoutContents(long id)
    {
        return this.modSettingsStorage.findById(id)
                .map(ModSettingsService::toDomain);
    }

    public Flux<ModSettingsHeader> getModSettingsWithoutContents()
    {
        return this.modSettingsStorage.findAll()
                .map(ModSettingsService::toDomain);
    }

    private static ModSettingsHeader toDomain(ModSettingsEntity entity)
    {
        return ModSettingsHeader.builder()
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
