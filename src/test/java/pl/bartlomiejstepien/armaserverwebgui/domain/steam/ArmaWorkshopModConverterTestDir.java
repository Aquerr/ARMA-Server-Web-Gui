package pl.bartlomiejstepien.armaserverwebgui.domain.steam;

import io.github.aquerr.steamwebapiclient.response.WorkShopQueryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopMod;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ArmaWorkshopModConverterTestDir
{
    private static final String TITLE = "Title";
    private static final String DESCRIPTION = "Description";
    private static final int FILE_ID = 1;
    private static final String PREVIEW_URL = "PreviewUrl";

    @InjectMocks
    private ArmaWorkshopModConverter converter;

    @Test
    void shouldConvertReturnNullWhenPassedNull()
    {
        ArmaWorkshopMod workshopMod = converter.convert(null);

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

        ArmaWorkshopMod armaWorkshopMod = converter.convert(publishedFileDetails);

        assertThat(armaWorkshopMod.getFileId()).isEqualTo(FILE_ID);
        assertThat(armaWorkshopMod.getTitle()).isEqualTo(TITLE);
        assertThat(armaWorkshopMod.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(armaWorkshopMod.getPreviewUrl()).isEqualTo(PREVIEW_URL);
    }
}
