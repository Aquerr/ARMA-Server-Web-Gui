package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModUpdateJob
{
    private final ModService modService;
    private final SteamService steamService;

    @Value("${aswg.job.mod-update.enabled}")
    private boolean enabled = false;

    @Scheduled(cron = "${aswg.job.mod-update.cron}")
    public void updateMods()
    {
        if (!enabled)
        {
            log.info("Mod update job is disabled. Skipping...");
            return;
        }

        log.info("Executing Mod Update Job");
        modService.getInstalledMods()
                .map(this::scheduleModUpdate)
                .subscribe();
    }

    private UUID scheduleModUpdate(InstalledModEntity installedModEntity)
    {
        return steamService.scheduleWorkshopModDownload(installedModEntity.getWorkshopFileId(), installedModEntity.getName(), false);
    }
}
