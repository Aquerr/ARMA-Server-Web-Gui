package pl.bartlomiejstepien.armaserverwebgui.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InstalledMod
{
    private long publishedFileId;
    private String name;
    private String directoryPath;
}
