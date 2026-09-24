package pl.bartlomiejstepien.armaserverwebgui.domain.purge;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.MissionService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModPresetService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModService;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataPurgeService
{
    private final ModService modService;
    private final ModPresetService modPresetService;
    private final MissionService missionService;

    public void purgeMissions(boolean deleteFiles)
    {
        log.info("Deleting all missions. Include files: {}", deleteFiles);
        this.missionService.deleteAllMissions(deleteFiles);
    }

    public void purgeMods(boolean deleteFiles)
    {
        log.info("Deleting all mods. Include files: {}", deleteFiles);
        this.modService.deleteAllMods(deleteFiles);
    }

    public void purgeModPresets()
    {
        log.info("Deleting all mod presets.");
        this.modPresetService.deleteAllPresets();
    }
}
