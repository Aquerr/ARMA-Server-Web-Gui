package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.ModSettings;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.ModSettingsHeader;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.ModSettingsEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

    public String getModSettingsContent(long id)
    {
        ModSettingsEntity modSettingsEntity = this.modSettingsStorage.findById(id);
        if (modSettingsEntity == null)
            throw new RuntimeException("Could not find settings for id = " + id);

        return modSettingsStorage.readModSettingsFileContent(modSettingsEntity.getName(), modSettingsEntity.isActive());
    }

    public ModSettingsHeader saveModSettings(ModSettings modSettings)
    {
        validateModSettingsName(modSettings);

        ModSettingsEntity entity = toEntity(modSettings);

        if (modSettings.isActive())
        {
            ModSettingsEntity activeModSettings = modSettingsStorage.findActive();
            if (activeModSettings != null)
            {
                String modSettingsContent = modSettingsStorage.readModSettingsFileContent(activeModSettings.getName(), true);
                if (!activeModSettings.getId().equals(modSettings.getId()))
                {
                    modSettingsStorage.saveModSettingsFileContent(activeModSettings.getName(), false, modSettingsContent);
                }
            }

            if (modSettings.getId() != null)
            {
                ModSettingsEntity modSettingsEntity = modSettingsStorage.findById(modSettings.getId());
                modSettingsStorage.deleteModSettingsFile(modSettingsEntity.getName(), modSettingsEntity.isActive());
            }

            modSettingsStorage.deactivateAll();
        }
        else
        {
            if (modSettings.getId() != null)
            {
                ModSettingsEntity modSettingsEntity = modSettingsStorage.findById(modSettings.getId());
                modSettingsStorage.deleteModSettingsFile(modSettingsEntity.getName(), modSettingsEntity.isActive());
            }
        }

        this.modSettingsStorage.saveModSettingsFileContent(modSettings.getName(), modSettings.isActive(), modSettings.getContent());
        return toDomain(this.modSettingsStorage.save(entity));
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

    public ModSettingsHeader getModSettingsWithoutContents(long id)
    {
        return Optional.ofNullable(this.modSettingsStorage.findById(id))
                .map(ModSettingsService::toDomain)
                .orElse(null);
    }

    public List<ModSettingsHeader> getModSettingsWithoutContents()
    {
        return this.modSettingsStorage.findAll().stream()
                .map(ModSettingsService::toDomain)
                .toList();
    }

    private static ModSettingsHeader toDomain(ModSettingsEntity entity)
    {
        return ModSettingsHeader.builder()
                .id(entity.getId())
                .name(entity.getName())
                .active(entity.isActive())
                .build();
    }

    public void deleteModSettings(long id)
    {
        ModSettingsEntity entity = this.modSettingsStorage.findById(id);
        if (entity != null)
        {
            this.modSettingsStorage.deleteModSettingsFile(entity.getName(), entity.isActive());
            this.modSettingsStorage.delete(entity);
        }
    }
}
