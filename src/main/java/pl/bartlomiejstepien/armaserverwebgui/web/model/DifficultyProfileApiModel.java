package pl.bartlomiejstepien.armaserverwebgui.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DifficultyProfileApiModel
{
    // aswg fields
    Integer id;
    String name;
    boolean active;

    Options options;

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Options
    {
        //actual game difficulty fields
        boolean reducedDamage;

        int groupIndicators;
        int friendlyTags;
        int enemyTags;
        int detectedMines;
        int commands;
        int waypoints;
        int tacticalPing;

        int weaponInfo;
        int stanceIndicator;
        boolean staminaBar;
        boolean weaponCrosshair;
        boolean visionAid;

        int thirdPersonView;
        boolean cameraShake;

        boolean scoreTable;
        boolean deathMessages;
        boolean vonId;

        boolean mapContentFriendly;
        boolean mapContentEnemy;
        boolean mapContentMines;

        boolean autoReport;
        boolean multipleSaves;

        int aiLevelPreset;

        String skillAI;
        String precisionAI;
    }
}
