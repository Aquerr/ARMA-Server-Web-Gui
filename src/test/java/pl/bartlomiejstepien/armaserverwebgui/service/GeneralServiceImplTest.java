package pl.bartlomiejstepien.armaserverwebgui.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.bartlomiejstepien.armaserverwebgui.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.model.GeneralProperties;
import pl.bartlomiejstepien.armaserverwebgui.storage.ServerConfigStorage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GeneralServiceImplTest
{
    private static final int MAX_PLAYERS = 10;
    private static final String PASSWORD = "test";
    private static final String LOG_FILE = "logFile";

    @Mock
    private ServerConfigStorage serverConfigStorage;
    @InjectMocks
    private GeneralServiceImpl generalService;

    @Captor
    private ArgumentCaptor<ArmaServerConfig> armaServerConfigArgumentCaptor;

    @Test
    void getGeneralPropertiesShouldUseServerConfigStorageAndReturnGeneralProperties()
    {
        ArmaServerConfig armaServerConfig = new ArmaServerConfig();
        armaServerConfig.setMaxPlayers(MAX_PLAYERS);
        given(serverConfigStorage.getServerConfig()).willReturn(armaServerConfig);

        GeneralProperties generalProperties = generalService.getGeneralProperties();

        assertThat(generalProperties.getMaxPlayers()).isEqualTo(MAX_PLAYERS);
    }

    @Test
    void saveGeneralPropertiesShouldUseServerConfigStorageAndSaveGeneralProperties()
    {
        ArmaServerConfig armaServerConfig = new ArmaServerConfig();
        armaServerConfig.setPassword(PASSWORD);
        armaServerConfig.setLogFile(LOG_FILE);
        given(serverConfigStorage.getServerConfig()).willReturn(armaServerConfig);

        generalService.saveGeneralProperties(prepareGeneralProperties());

        verify(serverConfigStorage).saveServerConfig(armaServerConfigArgumentCaptor.capture());
        assertThat(armaServerConfigArgumentCaptor.getValue().getMaxPlayers()).isEqualTo(MAX_PLAYERS);
    }

    @Test
    void saveGeneralPropertiesShouldNotModifyOtherConfigFiles()
    {
        ArmaServerConfig armaServerConfig = new ArmaServerConfig();
        armaServerConfig.setPassword(PASSWORD);
        armaServerConfig.setLogFile(LOG_FILE);
        given(serverConfigStorage.getServerConfig()).willReturn(armaServerConfig);

        generalService.saveGeneralProperties(prepareGeneralProperties());

        verify(serverConfigStorage).saveServerConfig(armaServerConfigArgumentCaptor.capture());
        ArmaServerConfig expectedArmaServerConfig = new ArmaServerConfig();
        expectedArmaServerConfig.setPassword(PASSWORD);
        expectedArmaServerConfig.setLogFile(LOG_FILE);
        expectedArmaServerConfig.setMaxPlayers(MAX_PLAYERS);
        assertThat(armaServerConfigArgumentCaptor.getValue()).isEqualTo(expectedArmaServerConfig);
    }

    private GeneralProperties prepareGeneralProperties()
    {
        return GeneralProperties.builder()
                .maxPlayers(MAX_PLAYERS)
                .build();
    }
}