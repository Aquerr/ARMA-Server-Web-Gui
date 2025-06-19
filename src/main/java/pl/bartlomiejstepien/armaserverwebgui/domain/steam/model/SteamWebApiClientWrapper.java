package pl.bartlomiejstepien.armaserverwebgui.domain.steam.model;

import io.github.aquerr.steamwebapiclient.SteamWebApiClient;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SteamWebApiClientWrapper
{
    private SteamWebApiClient steamWebApiClient;
}
