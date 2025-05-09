package pl.bartlomiejstepien.armaserverwebgui.application.security;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum AswgAuthority
{
    SERVER_START_STOP("SERVER_START_STOP"),
    CONSOLE_LOGS_VIEW("CONSOLE_LOGS_VIEW"),
    GENERAL_SETTINGS_VIEW("GENERAL_SETTINGS_VIEW"),
    GENERAL_SETTINGS_SAVE("GENERAL_SETTINGS_SAVE"),
    SECURITY_SETTINGS_VIEW("SECURITY_SETTINGS_VIEW"),
    SECURITY_SETTINGS_SAVE("SECURITY_SETTINGS_SAVE"),
    NETWORK_SETTINGS_VIEW("NETWORK_SETTINGS_VIEW"),
    NETWORK_SETTINGS_SAVE("NETWORK_SETTINGS_SAVE"),
    MODS_VIEW("MODS_VIEW"),
    MODS_DELETE("MODS_DELETE"),
    MODS_UPDATE("MODS_UPDATE"),
    MODS_UPLOAD("MODS_UPLOAD"),
    MOD_SETTINGS_VIEW("MOD_SETTINGS_VIEW"),
    MOD_SETTINGS_ADD("MOD_SETTINGS_ADD"),
    MOD_SETTINGS_DELETE("MOD_SETTINGS_DELETE"),
    MOD_SETTINGS_UPDATE("MOD_SETTINGS_UPDATE"),
    MISSIONS_VIEW("MISSIONS_VIEW"),
    MISSIONS_ADD("MISSIONS_ADD"),
    MISSIONS_DELETE("MISSIONS_DELETE"),
    MISSIONS_UPDATE("MISSIONS_UPDATE"),
    MISSIONS_UPLOAD("MISSIONS_UPLOAD"),
    DIFFICULTY_VIEW("DIFFICULTY_VIEW"),
    DIFFICULTY_ADD("DIFFICULTY_ADD"),
    DIFFICULTY_DELETE("DIFFICULTY_DELETE"),
    DIFFICULTY_UPDATE("DIFFICULTY_UPDATE"),
    WORKSHOP_INSTALL("WORKSHOP_INSTALL"),
    USERS_VIEW("USERS_VIEW"),
    USERS_ADD("USERS_ADD"),
    USERS_UPDATE("USERS_UPDATE"),
    USERS_DELETE("USERS_DELETE"),
    MOD_PRESETS_VIEW("MOD_PRESETS_VIEW"),
    MOD_PRESETS_ADD("MOD_PRESETS_ADD"),
    MOD_PRESETS_DELETE("MOD_PRESETS_DELETE"),
    MOD_PRESETS_SELECT("MOD_PRESETS_SELECT"),
    UNSAFE_OVERWRITE_STARTUP_PARAMS("UNSAFE_OVERWRITE_STARTUP_PARAMS")
    ;

    private final String code;

    AswgAuthority(String code)
    {
        this.code = code;
    }

    public static Optional<AswgAuthority> findByCode(String code)
    {
        return Arrays.stream(values())
                .filter(aswgAuthority -> aswgAuthority.getCode().equals(code))
                .findFirst();
    }
}
