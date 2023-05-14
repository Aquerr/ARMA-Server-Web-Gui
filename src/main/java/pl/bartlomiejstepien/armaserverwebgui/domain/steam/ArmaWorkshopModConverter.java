package pl.bartlomiejstepien.armaserverwebgui.domain.steam;

import io.github.aquerr.steamwebapiclient.response.WorkShopQueryResponse;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopMod;

@Component
public class ArmaWorkshopModConverter
{
    public ArmaWorkshopMod convert(WorkShopQueryResponse.QueryFilesResponse.PublishedFileDetails publishedFileDetails)
    {
        if (publishedFileDetails == null)
            return null;

        return ArmaWorkshopMod.builder()
                .fileId(Long.valueOf(publishedFileDetails.getPublishedFileId()))
                .title(publishedFileDetails.getTitle())
                .description(publishedFileDetails.getFileDescription())
                .previewUrl(publishedFileDetails.getPreviewUrl())
                .build();
    }
}
