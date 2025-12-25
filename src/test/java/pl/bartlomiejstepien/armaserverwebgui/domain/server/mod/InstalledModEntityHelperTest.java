package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.FileSystemMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.MetaCppFile;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.ModDirectory;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamService;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopMod;

import java.nio.file.Paths;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class InstalledModEntityHelperTest
{
    @Mock
    private ModDirectory modDirectory;

    @Mock
    private SteamService steamService;

    @InjectMocks
    private InstalledModEntityHelper installedModEntityHelper;

    @Test
    void shouldConvertInstalledFileSystemModToEntity()
    {
        //given
        MetaCppFile metaCppFile = mock(MetaCppFile.class);

        given(modDirectory.getModName()).willReturn("ACE");
        given(modDirectory.getMetaCppFile()).willReturn(metaCppFile);
        given(modDirectory.getPath()).willReturn(Paths.get("./ace-mod"));
        given(metaCppFile.getPublishedFileId()).willReturn(123456789L);
        given(metaCppFile.getTimestamp()).willReturn(5250320079498121474L);

        given(steamService.getWorkshopMod(123456789L)).willReturn(WorkshopMod.builder()
                .previewUrl("previewurl")
                .build());

        FileSystemMod fileSystemMod = prepareFileSystemMod();

        // when
        InstalledModEntity entity = installedModEntityHelper.toEntity(fileSystemMod);

        // then
        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo("ACE");
        assertThat(entity.getWorkshopFileId()).isEqualTo(123456789L);
        assertThat(entity.getDirectoryPath()).isEqualTo(Paths.get("./ace-mod").toAbsolutePath().toString());
        assertThat(entity.getCreatedDate()).isBeforeOrEqualTo(OffsetDateTime.now());
        assertThat(entity.getLastWorkshopUpdateDate()).isEqualTo(OffsetDateTime.parse("2024-10-01T19:01:47.073357Z"));
        assertThat(entity.getLastWorkshopUpdateAttemptDate()).isNull();
        assertThat(entity.getPreviewUrl()).isEqualTo("previewurl");
    }

    @Test
    void shouldNotSetModPreviewWhenCouldNotFetchModInfoFromWorkshop()
    {
        //given
        MetaCppFile metaCppFile = mock(MetaCppFile.class);

        given(modDirectory.getModName()).willReturn("A3 Thermal Improvement");
        given(modDirectory.getMetaCppFile()).willReturn(metaCppFile);
        given(modDirectory.getPath()).willReturn(Paths.get("./@A3-thermal-improvement"));
        given(metaCppFile.getPublishedFileId()).willReturn(2041057379L);
        given(metaCppFile.getTimestamp()).willReturn(5250320079498121474L);

        given(steamService.getWorkshopMod(2041057379L)).willReturn(null);

        FileSystemMod fileSystemMod = prepareFileSystemMod();

        // when
        InstalledModEntity entity = installedModEntityHelper.toEntity(fileSystemMod);

        // then
        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo("A3 Thermal Improvement");
        assertThat(entity.getWorkshopFileId()).isEqualTo(2041057379L);
        assertThat(entity.getDirectoryPath()).isEqualTo(Paths.get("./@A3-thermal-improvement").toAbsolutePath().toString());
        assertThat(entity.getCreatedDate()).isBeforeOrEqualTo(OffsetDateTime.now());
        assertThat(entity.getLastWorkshopUpdateDate()).isEqualTo(OffsetDateTime.parse("2024-10-01T19:01:47.073357Z"));
        assertThat(entity.getPreviewUrl()).isNull();
    }

    private FileSystemMod prepareFileSystemMod()
    {
        return FileSystemMod.from(modDirectory);
    }
}