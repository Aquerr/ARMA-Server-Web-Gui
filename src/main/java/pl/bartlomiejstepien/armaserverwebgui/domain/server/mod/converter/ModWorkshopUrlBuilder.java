package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.converter;

import org.springframework.stereotype.Component;

@Component
public class ModWorkshopUrlBuilder
{
    private static final String STEAM_WORKSHOP_URL = "https://steamcommunity.com/sharedfiles/filedetails/?id=";

    public String buildUrlForFileId(long fileId)
    {
        return STEAM_WORKSHOP_URL + fileId;
    }
}
