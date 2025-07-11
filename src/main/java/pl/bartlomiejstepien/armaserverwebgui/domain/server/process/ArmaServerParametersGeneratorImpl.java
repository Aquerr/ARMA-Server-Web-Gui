package pl.bartlomiejstepien.armaserverwebgui.domain.server.process;

import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty.DifficultyService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModSettingsService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.ModSettingsHeader;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.model.ArmaServerParameters;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.model.ServerExecutable;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ServerFiles;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class ArmaServerParametersGeneratorImpl implements ArmaServerParametersGenerator
{
    @Resource
    private final ASWGConfig aswgConfig;
    @Resource
    private final ModService modService;
    @Resource
    private final DifficultyService difficultyService;
    @Resource
    private final ModSettingsService modSettingsService;


    @Override
    public ArmaServerParameters generateParameters()
    {
        String serverExecToUse = ServerExecutable.getForBranch(aswgConfig.getServerBranch());

        List<InstalledModEntity> installedMods = modService.getInstalledMods();

        Set<String> modsDirs = installedMods.stream()
                .filter(InstalledModEntity::isEnabled)
                .filter(installedModEntity -> !installedModEntity.isServerMod())
                .map(InstalledModEntity::getModDirectoryName)
                .map(modDirName -> StringUtils.hasText(this.aswgConfig.getModsDirectoryPath()) ?
                        this.aswgConfig.getModsDirectoryPath() + File.separator + modDirName : modDirName)
                .collect(Collectors.toSet());

        Set<String> serverModsDirs = installedMods.stream()
                .filter(InstalledModEntity::isEnabled)
                .filter(InstalledModEntity::isServerMod)
                .map(InstalledModEntity::getModDirectoryName)
                .collect(Collectors.toSet());

        return ArmaServerParameters.builder()
                .overrideParameters(aswgConfig.getUnsafeProperties().getOverwriteStartupParamsValue())
                .profileName(difficultyService.getActiveDifficultyProfile())
                .customModSettings(modSettingsService.getModSettingsWithoutContents().stream().anyMatch(ModSettingsHeader::isActive))
                .serverDirectory(Paths.get(aswgConfig.getServerDirectoryPath()).toAbsolutePath().normalize().toString())
                .networkConfigPath(Paths.get(aswgConfig.getServerDirectoryPath()).resolve(ServerFiles.NETWORK_CONFIG).toAbsolutePath().normalize().toString())
                .serverConfigPath(Paths.get(aswgConfig.getServerDirectoryPath()).resolve(ServerFiles.SERVER_CONFIG).toAbsolutePath().normalize().toString())
                .executablePath(Paths.get(aswgConfig.getServerDirectoryPath()).resolve(serverExecToUse).toAbsolutePath().normalize().toString())
                .port(aswgConfig.getServerPort())
                .mods(modsDirs)
                .serverMods(serverModsDirs)
                .build();
    }
}
