package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.application.scheduling.AswgJob;
import pl.bartlomiejstepien.armaserverwebgui.application.scheduling.JobExecutionInfoService;
import pl.bartlomiejstepien.armaserverwebgui.domain.job.AswgJobNames;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamService;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ModUpdateJob extends AswgJob
{
    private final ModService modService;
    private final SteamService steamService;

    @Autowired
    public ModUpdateJob(ModService modService,
                        SteamService steamService,
                        JobExecutionInfoService jobExecutionInfoService)
    {
        super(jobExecutionInfoService);
        this.modService = modService;
        this.steamService = steamService;
    }

    @Override
    public void runJob()
    {
        if (!this.steamService.isSteamCmdInstalled())
        {
            log.warn("Steamcmd is not installed yet mod update is enabled. Fix this issue by properly configuring steamcmd.");
            return;
        }

        log.info("Executing Mod Update Job");
        scheduleModUpdate(modService.getInstalledMods().stream().collect(Collectors.toMap(InstalledModEntity::getWorkshopFileId, InstalledModEntity::getName)));
    }

    private void scheduleModUpdate(Map<Long, String> modIdWithName)
    {
        steamService.scheduleWorkshopModDownload(modIdWithName, false, "MOD_UPDATE_JOB");
    }

    @Override
    public String getName()
    {
        return AswgJobNames.MOD_UPDATE;
    }
}
