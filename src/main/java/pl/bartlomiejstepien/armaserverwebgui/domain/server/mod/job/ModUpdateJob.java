package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamService;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModUpdateJob
{
    private final ModService modService;
    private final SteamService steamService;

    @Value("${aswg.job.mod-update.enabled:false}")
    private boolean enabled = false;

    @Scheduled(cron = "${aswg.job.mod-update.cron:0 0 1 * * *}")
    public void updateMods()
    {
        if (!enabled)
        {
            log.info("Mod update job is disabled. Skipping...");
            return;
        }

        if (!this.steamService.isSteamCmdInstalled())
        {
            log.warn("Steamcmd is not installed yet mod update is enabled. Fix this issue by properly configuring steamcmd.");
            return;
        }

        log.info("Executing Mod Update Job");
        modService.getInstalledMods()
                .forEach(this::scheduleModUpdate);
    }

    private void scheduleModUpdate(InstalledModEntity installedModEntity)
    {
        steamService.scheduleWorkshopModDownload(installedModEntity.getWorkshopFileId(), installedModEntity.getName(), false);
    }
}
