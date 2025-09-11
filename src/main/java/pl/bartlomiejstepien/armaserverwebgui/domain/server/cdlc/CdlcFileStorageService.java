package pl.bartlomiejstepien.armaserverwebgui.domain.server.cdlc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.cdlc.dto.CdlcDirectory;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class CdlcFileStorageService
{
    private final ASWGConfig aswgConfig;

    public List<CdlcDirectory> findInstalledCdlcs(List<String> cdlcDirectoriesNames)
    {
        File serverDirectory = Paths.get(this.aswgConfig.getServerDirectoryPath()).toFile();
        String[] fileNames = serverDirectory.list();
        if (fileNames == null)
            return List.of();

        return Arrays.stream(fileNames)
                .filter(directoryName -> cdlcDirectoriesNames.contains(directoryName.toLowerCase()))
                .map(CdlcDirectory::new)
                .toList();
    }
}
