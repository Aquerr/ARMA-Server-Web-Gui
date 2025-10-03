package pl.bartlomiejstepien.armaserverwebgui.domain.steam.model;

public interface SteamTask
{
    Type getType();

    String getIssuer();

    enum Type
    {
        WORKSHOP_DOWNLOAD,
        WORKSHOP_BATCH_DOWNLOAD,
        GAME_UPDATE
    }
}
