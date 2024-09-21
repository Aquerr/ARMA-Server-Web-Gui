package pl.bartlomiejstepien.armaserverwebgui.web;

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
import pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty.DifficultyService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty.model.DifficultyProfile;
import pl.bartlomiejstepien.armaserverwebgui.web.model.DifficultyProfileApiModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/difficulties")
@AllArgsConstructor
public class DifficultyRestController
{
    private final DifficultyService difficultyService;

    @GetMapping
    public Flux<DifficultyProfileApiModel> getDifficulties()
    {
        return difficultyService.getDifficultyProfiles()
                .map(this::toApiModel);
    }

    @PostMapping
    public Mono<Void> saveDifficulty(@RequestBody DifficultyProfileApiModel difficultyProfileApiModel)
    {
        return difficultyService.saveDifficultyProfile(mapToDomainModel(difficultyProfileApiModel))
                .then();
    }

    @PutMapping("/{id}")
    public Mono<Void> updateDifficulty(@PathVariable("id") String identifier,
                                       @RequestBody DifficultyProfileApiModel difficultyProfileApiModel)
    {
        return difficultyService.saveDifficultyProfile(mapToDomainModel(difficultyProfileApiModel))
                .then();
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteDifficulty(@PathVariable("id") int id)
    {
        return difficultyService.deleteDifficultyProfile(id);
    }

    @DeleteMapping
    public Mono<Void> deleteDifficulty(@RequestParam("name") String name)
    {
        return difficultyService.deleteDifficultyProfile(name);
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
