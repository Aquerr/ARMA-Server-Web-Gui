package pl.bartlomiejstepien.armaserverwebgui.web.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class DifficultyProfileApiModel
{
    // aswg fields
    Integer id;
    String name;
    boolean active;

    Options options;

    @Value
    @Builder(toBuilder = true)
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
