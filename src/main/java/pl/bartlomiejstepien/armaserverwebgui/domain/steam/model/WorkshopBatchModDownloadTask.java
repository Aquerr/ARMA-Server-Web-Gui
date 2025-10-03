package pl.bartlomiejstepien.armaserverwebgui.domain.steam.model;

import lombok.Value;

import java.util.Map;

@Value
public class WorkshopBatchModDownloadTask implements SteamTask
{
    Map<Long, String> fileIdsWithTitles;
    boolean forced;
    String issuer;

    @Override
    public String getIssuer()
    {
        return issuer;
    }

    @Override
    public Type getType()
    {
        return Type.WORKSHOP_BATCH_DOWNLOAD;
    }
}
