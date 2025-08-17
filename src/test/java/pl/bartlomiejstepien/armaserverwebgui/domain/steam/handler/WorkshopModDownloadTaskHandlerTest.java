package pl.bartlomiejstepien.armaserverwebgui.domain.steam.handler;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.application.process.ExternalProcessHandler;
import pl.bartlomiejstepien.armaserverwebgui.application.process.ExternalProcessType;
import pl.bartlomiejstepien.armaserverwebgui.application.process.ProcessParameters;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.ModDirectory;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamWebApiService;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.CouldNotDownloadWorkshopModException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.SteamCmdPathNotSetException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.helper.SteamCmdModInstallHelper;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ModData;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.SteamCmdWorkshopDownloadParameters;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopModInstallSteamTask;
import pl.bartlomiejstepien.armaserverwebgui.repository.InstalledModRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class WorkshopModDownloadTaskHandlerTest
{
    private static final long MOD_ID = 123L;
    private static final String MOD_TITLE = "Test_Mod_Title";
    private static final String MOD_DIRECTORY_NAME = "Test_Mod_Directory";
    private static final String STEAMCMD_WORKSHOP_MOD_PATH = "target" + File.separator + "Steamcmd_path" + File.separator + "workshop" + File.separator + MOD_ID;
    private static final String STEAMCMD_PATH = "target" + File.separator + "Steamcmd_path";
    private static final String STEAMCMD_USERNAME = "steamcmd_username";
    private static final String STEAMCMD_PASSWORD = "steamcmd_password";

    @Mock
    private ASWGConfig aswgConfig;
    @Mock
    private SteamWebApiService steamWebApiService;
    @Mock
    private InstalledModRepository installedModRepository;
    @Mock
    private SteamCmdModInstallHelper steamCmdModInstallHelper;
    @Mock
    private ExternalProcessHandler externalProcessHandler;

    @InjectMocks
    private WorkshopModDownloadTaskHandler workshopModDownloadTaskHandler;

    @Captor
    private ArgumentCaptor<ProcessParameters> processParametersArgumentCaptor;

    @Captor
    private ArgumentCaptor<ModData> modDataArgumentCaptor;

    @Test
    void shouldSkipModUpdateWhenNotNeeded()
    {
        // given
        WorkshopMod workshopMod = prepareWorkshopMod();
        InstalledModEntity installedModEntity = prepareInstalledModEntity();

        given(steamWebApiService.getWorkshopMod(MOD_ID)).willReturn(workshopMod);
        given(installedModRepository.findByWorkshopFileId(MOD_ID)).willReturn(Optional.of(installedModEntity));
        given(steamCmdModInstallHelper.buildModDirectoryPath(MOD_DIRECTORY_NAME)).willReturn(Paths.get(MOD_DIRECTORY_NAME));
        given(steamCmdModInstallHelper.shouldUpdateMod(any(ModDirectory.class), eq(workshopMod), eq(installedModEntity), eq(false))).willReturn(false);

        // when
        workshopModDownloadTaskHandler.handle(new WorkshopModInstallSteamTask(MOD_ID, MOD_TITLE, false));

        // then
        verify(steamCmdModInstallHelper, times(0)).installDownloadedMod(any());
        verifyNoInteractions(aswgConfig);
        verifyNoInteractions(externalProcessHandler);
    }

    @Test
    void shouldThrowSteamCmdPathNotSetExceptionWhenSteamcmdPathNotSet()
    {
        // given
        WorkshopMod workshopMod = prepareWorkshopMod();
        InstalledModEntity installedModEntity = prepareInstalledModEntity();

        given(steamWebApiService.getWorkshopMod(MOD_ID)).willReturn(workshopMod);
        given(installedModRepository.findByWorkshopFileId(MOD_ID)).willReturn(Optional.of(installedModEntity));
        given(steamCmdModInstallHelper.buildModDirectoryPath(MOD_DIRECTORY_NAME)).willReturn(Paths.get(MOD_DIRECTORY_NAME));
        given(steamCmdModInstallHelper.shouldUpdateMod(any(ModDirectory.class), eq(workshopMod), eq(installedModEntity), eq(false))).willReturn(true);

        // when
        // then
        assertThrows(SteamCmdPathNotSetException.class, () -> workshopModDownloadTaskHandler.handle(new WorkshopModInstallSteamTask(MOD_ID, MOD_TITLE, false)));
    }

    @Test
    void shouldThrowCouldNotDownloadWorkshopModExceptionWhenModFolderDoesNotExistAfterDownload() throws IOException
    {
        // given
        WorkshopMod workshopMod = prepareWorkshopMod();
        InstalledModEntity installedModEntity = prepareInstalledModEntity();

        given(steamWebApiService.getWorkshopMod(MOD_ID)).willReturn(workshopMod);
        given(installedModRepository.findByWorkshopFileId(MOD_ID)).willReturn(Optional.of(installedModEntity));
        given(steamCmdModInstallHelper.buildModDirectoryPath(MOD_DIRECTORY_NAME)).willReturn(Paths.get(MOD_DIRECTORY_NAME));
        given(steamCmdModInstallHelper.shouldUpdateMod(any(ModDirectory.class), eq(workshopMod), eq(installedModEntity), eq(false))).willReturn(true);
        given(steamCmdModInstallHelper.buildWorkshopModDownloadPath(MOD_ID)).willReturn(Paths.get(STEAMCMD_WORKSHOP_MOD_PATH));
        given(aswgConfig.getSteamCmdPath()).willReturn(STEAMCMD_PATH);
        given(aswgConfig.getSteamCmdUsername()).willReturn(STEAMCMD_USERNAME);
        given(aswgConfig.getSteamCmdPassword()).willReturn(STEAMCMD_PASSWORD);

        // when
        // then
        assertThrows(CouldNotDownloadWorkshopModException.class, () -> workshopModDownloadTaskHandler.handle(new WorkshopModInstallSteamTask(MOD_ID, MOD_TITLE, false)));
    }

    @Test
    void shouldInstallMod() throws IOException
    {
        // given
        Path steamcmdWorkshopModPath = Paths.get(STEAMCMD_WORKSHOP_MOD_PATH);
        WorkshopMod workshopMod = prepareWorkshopMod();
        InstalledModEntity installedModEntity = prepareInstalledModEntity();

        given(steamWebApiService.getWorkshopMod(MOD_ID)).willReturn(workshopMod);
        given(installedModRepository.findByWorkshopFileId(MOD_ID)).willReturn(Optional.of(installedModEntity));
        given(steamCmdModInstallHelper.buildModDirectoryPath(MOD_DIRECTORY_NAME)).willReturn(Paths.get(MOD_DIRECTORY_NAME));
        given(steamCmdModInstallHelper.shouldUpdateMod(any(ModDirectory.class), eq(workshopMod), eq(installedModEntity), eq(false))).willReturn(true);
        given(steamCmdModInstallHelper.buildWorkshopModDownloadPath(MOD_ID)).willReturn(steamcmdWorkshopModPath);
        given(aswgConfig.getSteamCmdPath()).willReturn(STEAMCMD_PATH);
        given(aswgConfig.getSteamCmdUsername()).willReturn(STEAMCMD_USERNAME);
        given(aswgConfig.getSteamCmdPassword()).willReturn(STEAMCMD_PASSWORD);
        Files.createDirectories(steamcmdWorkshopModPath);

        // when
        workshopModDownloadTaskHandler.handle(new WorkshopModInstallSteamTask(MOD_ID, MOD_TITLE, false));

        // then
        verify(externalProcessHandler).handle(eq(new File("target")), processParametersArgumentCaptor.capture(), ExternalProcessType.STEAMCMD);
        assertThat(processParametersArgumentCaptor.getValue()).satisfies(processParameters -> {
            assertThat(processParameters).asInstanceOf(InstanceOfAssertFactories.type(SteamCmdWorkshopDownloadParameters.class)).satisfies(parameters -> {
                assertThat(parameters.getAppId()).isEqualTo(107410);
                assertThat(parameters.getFileId()).isEqualTo(MOD_ID);
                assertThat(parameters.getTitle()).isEqualTo(MOD_TITLE);
                assertThat(parameters.getSteamCmdPath()).isEqualTo(STEAMCMD_PATH);
                assertThat(parameters.getSteamUsername()).isEqualTo(STEAMCMD_USERNAME);
                assertThat(parameters.getSteamPassword()).isEqualTo(STEAMCMD_PASSWORD);
            });
        });
        verify(steamCmdModInstallHelper).installDownloadedMod(modDataArgumentCaptor.capture());
        assertThat(modDataArgumentCaptor.getValue()).satisfies(modData -> {
            assertThat(modData.getFileId()).isEqualTo(MOD_ID);
            assertThat(modData.getTitle()).isEqualTo(MOD_TITLE);
            assertThat(modData.getWorkshopMod()).isEqualTo(workshopMod);
            assertThat(modData.getSteamCmdModFolderPath()).isEqualTo(Paths.get(STEAMCMD_WORKSHOP_MOD_PATH));
            assertThat(modData.getInstalledModEntity()).isEqualTo(installedModEntity);
        });

        Files.deleteIfExists(steamcmdWorkshopModPath);
    }

    private WorkshopMod prepareWorkshopMod()
    {
        return WorkshopMod.builder()
                .fileId(MOD_ID)
                .build();
    }

    private InstalledModEntity prepareInstalledModEntity()
    {
        return InstalledModEntity.builder()
                .workshopFileId(MOD_ID)
                .directoryPath(MOD_DIRECTORY_NAME)
                .build();
    }
}