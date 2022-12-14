package pl.bartlomiejstepien.armaserverwebgui.domain.server.logging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.LoggingProperties;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.ServerConfigStorage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoggingServiceImplTest
{
    private static final String LOG_FILE_PATH = "log/logFilepath";
    private static final int MAX_PLAYERS = 15;
    private static final String PASSWORD = "testPassword";

    @Mock
    private ServerConfigStorage serverConfigStorage;
    @InjectMocks
    private LoggingServiceImpl loggingService;

    @Captor
    private ArgumentCaptor<ArmaServerConfig> armaServerConfigArgumentCaptor;

    @Test
    void getLoggingPropertiesShouldUseServerConfigStorageAndReturnLoggingProperties()
    {
        ArmaServerConfig armaServerConfig = new ArmaServerConfig();
        armaServerConfig.setLogFile(LOG_FILE_PATH);
        given(serverConfigStorage.getServerConfig()).willReturn(armaServerConfig);

        LoggingProperties loggingProperties = loggingService.getLoggingProperties();

        assertThat(loggingProperties.getLogFile()).isEqualTo(LOG_FILE_PATH);
    }

    @Test
    void saveLoggingPropertiesShouldSaveArmaServerConfigWithNewLoggingProperties()
    {
        given(serverConfigStorage.getServerConfig()).willReturn(new ArmaServerConfig());

        loggingService.saveLoggingProperties(prepareLoggingProperties());

        verify(serverConfigStorage).saveServerConfig(armaServerConfigArgumentCaptor.capture());
        assertThat(armaServerConfigArgumentCaptor.getValue().getLogFile()).isEqualTo(LOG_FILE_PATH);
    }

    @Test
    void saveLoggingPropertiesShouldNotModifyOtherConfigFiles()
    {
        ArmaServerConfig armaServerConfig = new ArmaServerConfig();
        armaServerConfig.setPassword(PASSWORD);
        armaServerConfig.setMaxPlayers(MAX_PLAYERS);
        given(serverConfigStorage.getServerConfig()).willReturn(armaServerConfig);

        loggingService.saveLoggingProperties(prepareLoggingProperties());

        verify(serverConfigStorage).saveServerConfig(armaServerConfigArgumentCaptor.capture());
        ArmaServerConfig expectedArmaServerConfig = new ArmaServerConfig();
        expectedArmaServerConfig.setPassword(PASSWORD);
        expectedArmaServerConfig.setLogFile(LOG_FILE_PATH);
        expectedArmaServerConfig.setMaxPlayers(MAX_PLAYERS);
        assertThat(armaServerConfigArgumentCaptor.getValue()).isEqualTo(expectedArmaServerConfig);
    }

    private LoggingProperties prepareLoggingProperties()
    {
        return LoggingProperties.builder()
                .logFile(LOG_FILE_PATH)
                .build();
    }
}