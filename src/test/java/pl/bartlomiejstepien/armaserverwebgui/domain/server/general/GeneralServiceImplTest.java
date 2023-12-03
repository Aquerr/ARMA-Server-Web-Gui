package pl.bartlomiejstepien.armaserverwebgui.domain.server.general;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.general.model.GeneralProperties;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.ServerConfigStorage;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GeneralServiceImplTest
{
    private static final String HOSTNAME = "Hostname";
    private static final int MAX_PLAYERS = 10;
    private static final String PASSWORD = "test";
    private static final String LOG_FILE = "logFile";
    private static final List<String> MOTD = Arrays.asList("testmod");

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
        armaServerConfig.setMotd(new String[]{"testmod"});
        given(serverConfigStorage.getServerConfig()).willReturn(armaServerConfig);

        generalService.saveGeneralProperties(prepareGeneralProperties());

        verify(serverConfigStorage).saveServerConfig(armaServerConfigArgumentCaptor.capture());
        ArmaServerConfig expectedArmaServerConfig = new ArmaServerConfig();
        expectedArmaServerConfig.setHostname(HOSTNAME);
        expectedArmaServerConfig.setPassword(PASSWORD);
        expectedArmaServerConfig.setLogFile(LOG_FILE);
        expectedArmaServerConfig.setMaxPlayers(MAX_PLAYERS);
        expectedArmaServerConfig.setMotd(new String[]{"testmod"});
        expectedArmaServerConfig.setPersistent(1);
        expectedArmaServerConfig.setDrawingInMap("true");
        assertThat(armaServerConfigArgumentCaptor.getValue()).isEqualTo(expectedArmaServerConfig);
    }

    private GeneralProperties prepareGeneralProperties()
    {
        return GeneralProperties.builder()
                .hostname(HOSTNAME)
                .maxPlayers(MAX_PLAYERS)
                .motd(MOTD)
                .persistent(true)
                .motdInterval(5)
                .drawingInMap(true)
                .build();
    }
}
