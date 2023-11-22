package pl.bartlomiejstepien.armaserverwebgui.domain.steam.model;

import lombok.Value;

@Value
public class GameUpdateSteamTask implements SteamTask
{
    @Override
    public Type getType()
    {
        return Type.GAME_UPDATE;
    }
}
