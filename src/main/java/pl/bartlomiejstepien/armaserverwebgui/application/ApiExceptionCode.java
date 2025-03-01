package pl.bartlomiejstepien.armaserverwebgui.application;

public enum ApiExceptionCode
{
    BAD_AUTH_TOKEN,
    AUTH_TOKEN_EXPIRED,
    AUTH_TOKEN_MISSING,
    SERVER_ALREADY_RUNNING,
    SERVER_NOT_INSTALLED,
    NOT_ALLOWED_FILE_TYPE,

    MISSION_NOT_FOUND,
    MISSION_ALREADY_EXISTS,

    MOD_ALREADY_EXISTS
}
