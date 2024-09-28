package pl.bartlomiejstepien.armaserverwebgui.domain.steam;

import io.github.aquerr.steamwebapiclient.response.PublishedFileDetailsResponse;
import io.github.aquerr.steamwebapiclient.response.WorkShopQueryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopMod;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class WorkshopModConverterTestDir
{
    private static final String TITLE = "Title";
    private static final String DESCRIPTION = "Description";
    private static final int FILE_ID = 1;
    private static final String PREVIEW_URL = "PreviewUrl";

    @InjectMocks
    private ArmaWorkshopModConverter converter;

    @Test
    void shouldConvertReturnNullWhenPassedNullPublishedFileDetailsWorkshop()
    {
        WorkshopMod workshopMod = converter.convert((WorkShopQueryResponse.QueryFilesResponse.PublishedFileDetails) null);

        assertThat(workshopMod).isNull();
    }

    @Test
    void shouldConvertReturnNullWhenPassedNullPublishedFileDetails()
    {
        WorkshopMod workshopMod = converter.convert((PublishedFileDetailsResponse.QueryFilesResponse.PublishedFileDetails) null);

        assertThat(workshopMod).isNull();
    }

    @Test
    void shouldConvertPublishedFileDetails()
    {
        WorkShopQueryResponse.QueryFilesResponse.PublishedFileDetails publishedFileDetails = new WorkShopQueryResponse.QueryFilesResponse.PublishedFileDetails();
        publishedFileDetails.setTitle(TITLE);
        publishedFileDetails.setPublishedFileId(String.valueOf(FILE_ID));
        publishedFileDetails.setFileDescription(DESCRIPTION);
        publishedFileDetails.setPreviewUrl(PREVIEW_URL);

        WorkshopMod workshopMod = converter.convert(publishedFileDetails);

        assertThat(workshopMod.getFileId()).isEqualTo(FILE_ID);
        assertThat(workshopMod.getTitle()).isEqualTo(TITLE);
        assertThat(workshopMod.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(workshopMod.getPreviewUrl()).isEqualTo(PREVIEW_URL);
    }
}
