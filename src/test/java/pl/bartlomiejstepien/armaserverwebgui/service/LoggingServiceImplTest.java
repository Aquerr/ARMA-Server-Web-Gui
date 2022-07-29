package pl.bartlomiejstepien.armaserverwebgui.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.bartlomiejstepien.armaserverwebgui.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.model.LoggingProperties;
import pl.bartlomiejstepien.armaserverwebgui.storage.ServerConfigStorage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoggingServiceImplTest
{
    private static final String LOG_FILE_PATH = "log/logFilepath";

    @Mock
    private ServerConfigStorage serverConfigStorage;
    @InjectMocks
    private LoggingServiceImpl loggingService;

    @Captor
    private ArgumentCaptor<ArmaServerConfig> armaServerConfigArgumentCaptor;

    @Test
    void getLoggingSectionDataShouldUseServerConfigStorageAndReturnLoggingSectionData()
    {
        given(serverConfigStorage.getServerConfig()).willReturn(prepareArmaServerConfig());

        LoggingProperties loggingProperties = loggingService.getLoggingSectionData();

        assertThat(loggingProperties.getLogFile()).isEqualTo(LOG_FILE_PATH);
    }

    @Test
    void saveLoggingSectionDataShouldSaveArmaServerConfigWithNewLoggingSectionData()
    {
        given(serverConfigStorage.getServerConfig()).willReturn(new ArmaServerConfig());

        loggingService.saveLoggingSectionData(prepareLoggingSectionData());

        verify(serverConfigStorage).saveServerConfig(armaServerConfigArgumentCaptor.capture());
        assertThat(armaServerConfigArgumentCaptor.getValue().getLogFile()).isEqualTo(LOG_FILE_PATH);
    }

    private LoggingProperties prepareLoggingSectionData()
    {
        return LoggingProperties.builder()
                .logFile(LOG_FILE_PATH)
                .build();
    }

    private ArmaServerConfig prepareArmaServerConfig()
    {
        ArmaServerConfig armaServerConfig = new ArmaServerConfig();
        armaServerConfig.setLogFile(LOG_FILE_PATH);
        return armaServerConfig;
    }
}