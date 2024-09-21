package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model;

import lombok.Data;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.annotation.CfgProperty;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.type.PropertyType;

@Data
public class DifficultyConfig
{
    @CfgProperty(name = "DifficultyPresets", isClass = true, type = PropertyType.CLASS)
    private DifficultyPresets difficultyPresets = new DifficultyPresets();

    @Data
    public static class DifficultyPresets
    {
        @CfgProperty(name = "CustomDifficulty", isClass = true, type = PropertyType.CLASS)
        private CustomDifficulty customDifficulty = new CustomDifficulty();

        @CfgProperty(name = "CustomAILevel", isClass = true, type = PropertyType.CLASS)
        private CustomDifficulty.CustomAiLevel customAiLevel = new CustomDifficulty.CustomAiLevel();

        @Data
        public static class CustomDifficulty
        {
            @CfgProperty(name = "Options", isClass = true, type = PropertyType.CLASS)
            private Options options = new Options();

            @CfgProperty(name = "aiLevelPreset", type = PropertyType.INTEGER)
            private int aiLevelPreset;

            @Data
            public static class Options
            {
                @CfgProperty(name = "reducedDamage", type = PropertyType.INTEGER)
                int reducedDamage = 0;

                @CfgProperty(name = "groupIndicators", type = PropertyType.INTEGER)
                int groupIndicators = 1;
                @CfgProperty(name = "friendlyTags", type = PropertyType.INTEGER)
                int friendlyTags = 1;
                @CfgProperty(name = "enemyTags", type = PropertyType.INTEGER)
                int enemyTags = 1;
                @CfgProperty(name = "detectedMines", type = PropertyType.INTEGER)
                int detectedMines = 1;
                @CfgProperty(name = "commands", type = PropertyType.INTEGER)
                int commands = 1;
                @CfgProperty(name = "waypoints", type = PropertyType.INTEGER)
                int waypoints = 1;
                @CfgProperty(name = "tacticalPing", type = PropertyType.INTEGER)
                int tacticalPing = 1;

                @CfgProperty(name = "weaponInfo", type = PropertyType.INTEGER)
                int weaponInfo = 1;
                @CfgProperty(name = "stanceIndicator", type = PropertyType.INTEGER)
                int stanceIndicator = 1;
                @CfgProperty(name = "staminaBar", type = PropertyType.INTEGER)
                int staminaBar = 1;
                @CfgProperty(name = "weaponCrosshair", type = PropertyType.INTEGER)
                int weaponCrosshair = 1;
                @CfgProperty(name = "visionAid", type = PropertyType.INTEGER)
                int visionAid = 1;

                @CfgProperty(name = "thirdPersonView", type = PropertyType.INTEGER)
                int thirdPersonView = 1;
                @CfgProperty(name = "cameraShake", type = PropertyType.INTEGER)
                int cameraShake = 1;

                @CfgProperty(name = "scoreTable", type = PropertyType.INTEGER)
                int scoreTable = 1;
                @CfgProperty(name = "deathMessages", type = PropertyType.INTEGER)
                int deathMessages = 1;
                @CfgProperty(name = "vonID", type = PropertyType.INTEGER)
                int vonId = 1;

                @CfgProperty(name = "mapContentFriendly", type = PropertyType.INTEGER)
                int mapContentFriendly = 1;
                @CfgProperty(name = "mapContentEnemy", type = PropertyType.INTEGER)
                int mapContentEnemy = 1;
                @CfgProperty(name = "mapContentMines", type = PropertyType.INTEGER)
                int mapContentMines = 1;

                @CfgProperty(name = "autoReport", type = PropertyType.INTEGER)
                int autoReport = 1;
                @CfgProperty(name = "multipleSaves", type = PropertyType.INTEGER)
                int multipleSaves = 1;
            }

            @Data
            public static class CustomAiLevel
            {
                @CfgProperty(name = "skillAI", type = PropertyType.RAW_STRING)
                private String skillAI = "0.5";
                @CfgProperty(name = "precisionAI", type = PropertyType.RAW_STRING)
                private String precisionAI = "0.5";
            }
        }
    }
}
