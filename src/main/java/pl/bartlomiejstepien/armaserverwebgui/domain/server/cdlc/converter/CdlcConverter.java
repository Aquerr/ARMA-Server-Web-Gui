package pl.bartlomiejstepien.armaserverwebgui.domain.server.cdlc.converter;

import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.cdlc.dto.Cdlc;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.cdlc.dto.CdlcDirectory;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.cdlc.model.CdlcEntity;

import java.util.ArrayList;
import java.util.List;

@Component
public class CdlcConverter
{
    public Cdlc convert(CdlcEntity cdlcEntity, boolean fileExists)
    {
        if (cdlcEntity == null)
            return null;

        return Cdlc.builder()
                .id(cdlcEntity.getId())
                .name(cdlcEntity.getName())
                .directoryName(cdlcEntity.getDirectoryName())
                .enabled(cdlcEntity.isEnabled())
                .fileExists(fileExists)
                .build();
    }

    public List<Cdlc> convert(List<CdlcEntity> cdlcEntities, List<CdlcDirectory> cdlcDirectories)
    {
        if (cdlcEntities.isEmpty())
            return List.of();

        final List<Cdlc> cdlcs = new ArrayList<>(cdlcEntities.size());
        for (CdlcEntity cdlcEntity : cdlcEntities)
        {
            cdlcs.add(convert(cdlcEntity, cdlcDirectories.stream()
                    .anyMatch(cdlcDirectory ->
                            cdlcDirectory.getName().equalsIgnoreCase(cdlcEntity.getDirectoryName()))));
        }
        return cdlcs;
    }
}
