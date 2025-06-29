package pl.bartlomiejstepien.armaserverwebgui.domain.steam.model;

import lombok.Value;

import java.nio.file.Path;
import java.util.Map;

@Value
public class ModDownloadResult
{
    Map<Long, Path> successMods;
    Map<Long, Path> failedMods;
}