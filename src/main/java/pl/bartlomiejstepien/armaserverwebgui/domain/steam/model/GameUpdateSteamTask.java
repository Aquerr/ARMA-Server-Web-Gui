package pl.bartlomiejstepien.armaserverwebgui.domain.steam.model;

import lombok.Value;

@Value
public class GameUpdateSteamTask implements SteamTask
{
    String issuer;

    @Override
    public String getIssuer()
    {
        return this.issuer;
    }

    @Override
    public Type getType()
    {
        return Type.GAME_UPDATE;
    }
}
