package pl.bartlomiejstepien.armaserverwebgui.domain.steam.model;

import lombok.Value;

import java.util.Map;

@Value
public class WorkshopBatchModDownloadTask implements SteamTask
{
    Map<Long, String> fileIdsWithTitles;
    boolean forced;

    @Override
    public Type getType()
    {
        return Type.WORKSHOP_BATCH_DOWNLOAD;
    }
}
