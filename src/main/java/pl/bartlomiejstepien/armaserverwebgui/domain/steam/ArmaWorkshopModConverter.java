package pl.bartlomiejstepien.armaserverwebgui.domain.steam;

import io.github.aquerr.steamwebapiclient.response.shared.PublishedFileDetails;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopMod;

@Component
public class ArmaWorkshopModConverter
{
    public WorkshopMod convert(PublishedFileDetails publishedFileDetails)
    {
        if (publishedFileDetails == null)
            return null;

        return WorkshopMod.builder()
                .fileId(Long.valueOf(publishedFileDetails.getPublishedFileId()))
                .title(publishedFileDetails.getTitle())
                .description(publishedFileDetails.getFileDescription())
                .previewUrl(publishedFileDetails.getPreviewUrl())
                .modWorkshopUrl("https://steamcommunity.com/sharedfiles/filedetails/?id=" + publishedFileDetails.getPublishedFileId())
                .dependencies(publishedFileDetails.getChildren().stream()
                        .map(PublishedFileDetails.ChildItem::getPublishedFileId)
                        .map(Long::valueOf)
                        .toList())
                .lastUpdate(publishedFileDetails.getUpdatedDateTime().toOffsetDateTime())
                .build();
    }
}
