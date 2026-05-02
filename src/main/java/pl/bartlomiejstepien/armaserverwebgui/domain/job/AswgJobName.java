package pl.bartlomiejstepien.armaserverwebgui.domain.job;

import java.util.Arrays;
import java.util.Optional;

public enum AswgJobName
{
    MOD_UPDATE("MOD_UPDATE", "Updates all mods using Steamcmd."),
    INSTALL_DELETE_MODS("INSTALL_DELETE_MODS", "Installs new mods detected inside mods folder to the DB " +
            "and deletes mods from DB that corresponding files have been deleted."),
    DIFFICULTY_SCAN("DIFFICULTY_SCAN", "Scans for new difficulty profile files and adds them to the database."),
    MOD_SETTINGS_SCAN("MOD_SETTINGS_SCAN", "Scans for new mod settings files and adds them to the database."),
    MISSIONS_SCANNER("MISSIONS_SCANNER", "Scans for new missions files inside missions directory and adds them to the database.");

    private final String code;
    private final String description;

    AswgJobName(String code, String description)
    {
        this.code = code;
        this.description = description;
    }

    public String getCode()
    {
        return code;
    }

    public String getDescription()
    {
        return description;
    }

    public static Optional<AswgJobName> findByCode(String nullableCode)
    {
                return Optional.ofNullable(nullableCode)
                .flatMap(code -> Arrays.stream(values())
                        .filter(aswgJobName -> aswgJobName.getCode().equals(code.toUpperCase()))
                        .findFirst());
    }
}
