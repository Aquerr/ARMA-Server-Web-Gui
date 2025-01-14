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
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamUtils;
import reactor.core.publisher.Mono;

import java.io.File;
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
    public Mono<ArmaServerParameters> generateParameters()
    {
        String serverExecToUse = calculateServerExecutable(aswgConfig.getServerBranch());

        List<InstalledModEntity> installedMods = modService.getInstalledMods()
                .collectList()
                .toFuture()
                .join();

        Set<String> modsDirs = installedMods.stream()
                .filter(InstalledModEntity::isEnabled)
                .filter(installedModEntity -> !installedModEntity.isServerMod())
                .map(InstalledModEntity::getModDirectoryName)
                .map(modDirName -> StringUtils.hasText(this.aswgConfig.getModsDirectoryPath()) ? this.aswgConfig.getModsDirectoryPath() + File.separator + modDirName : modDirName)
                .collect(Collectors.toSet());

        Set<String> serverModsDirs = installedMods.stream()
                .filter(InstalledModEntity::isEnabled)
                .filter(InstalledModEntity::isServerMod)
                .map(InstalledModEntity::getModDirectoryName)
                .collect(Collectors.toSet());

        return Mono.just(ArmaServerParameters.builder())
                .zipWith(difficultyService.getActiveDifficultyProfile(), ArmaServerParameters.ArmaServerParametersBuilder::profileName)
                .zipWith(modSettingsService.getModSettingsWithoutContents().any(ModSettingsHeader::isActive), (builder, header) -> builder.customModSettings(true))
                .map(builder -> builder.serverDirectory(aswgConfig.getServerDirectoryPath())
                    .networkConfigPath(aswgConfig.getServerDirectoryPath() + File.separator + ServerFiles.NETWORK_CONFIG)
                    .serverConfigPath(aswgConfig.getServerDirectoryPath() + File.separator + ServerFiles.SERVER_CONFIG)
                    .executablePath(aswgConfig.getServerDirectoryPath() + File.separator + serverExecToUse)
                    .port(aswgConfig.getServerPort())
                    .mods(modsDirs)
                    .serverMods(serverModsDirs)
                    .build()
                );
    }

    private boolean is64Bit()
    {
        return System.getProperty("os.arch").contains("64");
    }

    private String calculateServerExecutable(String serverBranch)
    {
        if (SteamUtils.ARMA_BRANCH_PROFILING.equals(serverBranch))
        {
            return is64Bit() ? ServerExecutable.PROFILING_BRANCH.getGetServerExecutable64bit() : ServerExecutable.PROFILING_BRANCH.getServerExecutable();
        }
        return is64Bit() ? ServerExecutable.MAIN_BRANCH.getGetServerExecutable64bit() : ServerExecutable.MAIN_BRANCH.getServerExecutable();
    }
}
