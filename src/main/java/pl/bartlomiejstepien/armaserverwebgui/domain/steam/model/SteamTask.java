package pl.bartlomiejstepien.armaserverwebgui.domain.steam.model;

public interface SteamTask
{
    Type getType();

    enum Type
    {
        WORKSHOP_DOWNLOAD,
        GAME_UPDATE
    }
}
