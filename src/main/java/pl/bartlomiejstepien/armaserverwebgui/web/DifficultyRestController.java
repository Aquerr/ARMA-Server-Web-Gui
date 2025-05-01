package pl.bartlomiejstepien.armaserverwebgui.web;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionDifficultyAdd;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionDifficultyDelete;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionDifficultyUpdate;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionDifficultyView;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty.DifficultyService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty.model.DifficultyProfile;
import pl.bartlomiejstepien.armaserverwebgui.web.model.DifficultyProfileApiModel;

@RestController
@RequestMapping("/api/v1/difficulties")
@AllArgsConstructor
public class DifficultyRestController
{
    private final DifficultyService difficultyService;

    @HasPermissionDifficultyView
    @GetMapping
    public List<DifficultyProfileApiModel> getDifficulties()
    {
        return difficultyService.getDifficultyProfiles().stream()
                .map(this::toApiModel)
                .toList();
    }

    @HasPermissionDifficultyAdd
    @PostMapping
    public void saveDifficulty(@RequestBody DifficultyProfileApiModel difficultyProfileApiModel)
    {
        difficultyService.saveDifficultyProfile(mapToDomainModel(difficultyProfileApiModel));
    }

    @HasPermissionDifficultyUpdate
    @PutMapping("/{id}")
    public void updateDifficulty(@PathVariable("id") String identifier,
                                 @RequestBody DifficultyProfileApiModel difficultyProfileApiModel)
    {
        difficultyService.saveDifficultyProfile(mapToDomainModel(difficultyProfileApiModel));
    }

    @HasPermissionDifficultyDelete
    @DeleteMapping("/{id}")
    public void deleteDifficulty(@PathVariable("id") int id)
    {
        difficultyService.deleteDifficultyProfile(id);
    }

    @HasPermissionDifficultyDelete
    @DeleteMapping
    public void deleteDifficulty(@RequestParam("name") String name)
    {
        difficultyService.deleteDifficultyProfile(name);
    }

    private DifficultyProfile mapToDomainModel(DifficultyProfileApiModel difficultyProfileApiModel)
    {
        DifficultyProfileApiModel.Options options = difficultyProfileApiModel.getOptions();
        return DifficultyProfile.builder()
                .id(difficultyProfileApiModel.getId())
                .name(difficultyProfileApiModel.getName())
                .active(difficultyProfileApiModel.isActive())
                .options(DifficultyProfile.Options.builder()
                        .aiLevelPreset(options.getAiLevelPreset())
                        .reducedDamage(options.isReducedDamage())
                        .groupIndicators(options.getGroupIndicators())
                        .friendlyTags(options.getFriendlyTags())
                        .enemyTags(options.getEnemyTags())
                        .detectedMines(options.getDetectedMines())
                        .commands(options.getCommands())
                        .waypoints(options.getWaypoints())

                        .weaponInfo(options.getWeaponInfo())
                        .stanceIndicator(options.getStanceIndicator())
                        .staminaBar(options.isStaminaBar())
                        .weaponCrosshair(options.isWeaponCrosshair())
                        .visionAid(options.isVisionAid())

                        .thirdPersonView(options.getThirdPersonView())
                        .cameraShake(options.isCameraShake())

                        .scoreTable(options.isScoreTable())
                        .deathMessages(options.isDeathMessages())
                        .vonId(options.isVonId())

                        .mapContentFriendly(options.isMapContentFriendly())
                        .mapContentEnemy(options.isMapContentEnemy())
                        .mapContentMines(options.isMapContentMines())
                        .tacticalPing(options.getTacticalPing())

                        .autoReport(options.isAutoReport())
                        .multipleSaves(options.isMultipleSaves())
                        .build())
                .build();
    }

    private DifficultyProfileApiModel toApiModel(DifficultyProfile difficultyProfile)
    {
        DifficultyProfile.Options options = difficultyProfile.getOptions();
        return DifficultyProfileApiModel.builder()
                .id(difficultyProfile.getId())
                .name(difficultyProfile.getName())
                .active(difficultyProfile.isActive())
                .options(DifficultyProfileApiModel.Options.builder()
                        .aiLevelPreset(options.getAiLevelPreset())
                        .reducedDamage(options.isReducedDamage())
                        .groupIndicators(options.getGroupIndicators())
                        .friendlyTags(options.getFriendlyTags())
                        .enemyTags(options.getEnemyTags())
                        .detectedMines(options.getDetectedMines())
                        .commands(options.getCommands())
                        .waypoints(options.getWaypoints())

                        .weaponInfo(options.getWeaponInfo())
                        .stanceIndicator(options.getStanceIndicator())
                        .staminaBar(options.isStaminaBar())
                        .weaponCrosshair(options.isWeaponCrosshair())
                        .visionAid(options.isVisionAid())

                        .thirdPersonView(options.getThirdPersonView())
                        .cameraShake(options.isCameraShake())

                        .scoreTable(options.isScoreTable())
                        .deathMessages(options.isDeathMessages())
                        .vonId(options.isVonId())

                        .mapContentFriendly(options.isMapContentFriendly())
                        .mapContentEnemy(options.isMapContentEnemy())
                        .mapContentMines(options.isMapContentMines())
                        .tacticalPing(options.getTacticalPing())

                        .autoReport(options.isAutoReport())
                        .multipleSaves(options.isMultipleSaves())
                        .build())
                .build();
    }
}
