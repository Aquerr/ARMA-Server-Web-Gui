package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.converter;

import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.ModPreset;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.ModPresetEntity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ModPresetConverter
{
    public ModPreset convert(ModPresetEntity entity, List<ModPresetEntity.EntryEntity> entries)
    {
        if (entity == null)
            return null;

        return ModPreset.builder()
                .id(entity.getId())
                .name(entity.getName())
                .entries(convertToDtos(entries))
                .build();
    }

    public List<ModPreset.Entry> convertToDtos(List<ModPresetEntity.EntryEntity> entries)
    {
        if (entries == null || entries.isEmpty())
            return Collections.emptyList();

        return entries.stream()
                .map(this::convert)
                .toList();
    }

    public ModPreset.Entry convert(ModPresetEntity.EntryEntity entryEntity)
    {
        if (entryEntity == null)
            return null;

        return ModPreset.Entry.builder()
                .id(entryEntity.getId())
                .name(entryEntity.getName())
                .modId(entryEntity.getModId())
                .modPresetId(entryEntity.getModPresetId())
                .build();
    }

    public ModPresetEntity convert(ModPreset preset)
    {
        if (preset == null)
            return null;

        return ModPresetEntity.builder()
                .id(preset.getId())
                .name(preset.getName())
                .build();
    }

    public List<ModPresetEntity.EntryEntity> convertToEntities(List<ModPreset.Entry> entries)
    {
        if (entries == null || entries.isEmpty())
            return Collections.emptyList();

        return entries.stream()
                .map(this::convert)
                .toList();
    }

    public ModPresetEntity.EntryEntity convert(ModPreset.Entry entry)
    {
        if (entry == null)
            return null;

        return ModPresetEntity.EntryEntity.builder()
                .id(entry.getId())
                .name(entry.getName())
                .modId(entry.getModId())
                .modPresetId(entry.getModPresetId())
                .build();
    }
}
