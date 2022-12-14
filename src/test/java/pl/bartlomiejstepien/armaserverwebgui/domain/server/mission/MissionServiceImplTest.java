package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.multipart.FilePart;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.Mission;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.Missions;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.exception.MissionFileAlreadyExistsException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.ServerConfigStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.mission.MissionStorage;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MissionServiceImplTest
{
    private static final String MISSION_NAME_1 = "MyMission";
    private static final String MISSION_NAME_2 = "MySecondMission";

    private static final Mission MISSION_1 = new Mission(MISSION_NAME_1, Collections.emptyMap());
    private static final Mission MISSION_2 = new Mission(MISSION_NAME_2, Collections.emptyMap());

    @Mock
    private MissionStorage missionStorage;
    @Mock
    private ServerConfigStorage serverConfigStorage;
    @InjectMocks
    private MissionServiceImpl missionService;

    @Captor
    private ArgumentCaptor<ArmaServerConfig> armaServerConfigArgumentCaptor;

    @Test
    void shouldSaveMissionFile() throws IOException
    {
        FilePart filePart = mock(FilePart.class);
        given(filePart.filename()).willReturn(MISSION_NAME_1);
        given(missionStorage.doesMissionExists(MISSION_NAME_1)).willReturn(false);

        missionService.save(filePart);

        verify(missionStorage, times(1)).save(filePart);
    }

    @Test
    void shouldThrowMissionFileAlreadyExistsExceptionWhenMissionFileExists()
    {
        FilePart filePart = mock(FilePart.class);
        given(filePart.filename()).willReturn(MISSION_NAME_1);
        given(missionStorage.doesMissionExists(MISSION_NAME_1)).willReturn(true);

        assertThrows(MissionFileAlreadyExistsException.class, () -> missionService.save(filePart));
    }

    @Test
    void shouldSaveEnabledMissionList()
    {
        List<Mission> missions = List.of(MISSION_1, MISSION_2);
        given(serverConfigStorage.getServerConfig()).willReturn(prepareArmaServerConfig(List.of()));

        missionService.saveEnabledMissionList(missions);

        verify(serverConfigStorage).saveServerConfig(armaServerConfigArgumentCaptor.capture());
        assertThat(armaServerConfigArgumentCaptor.getValue().getMissions().getMissions())
                .extracting(ArmaServerConfig.Missions.Mission::getTemplate)
                .containsExactlyElementsOf(List.of(MISSION_NAME_1, MISSION_NAME_2));
    }

    @Test
    void shouldGetMissions()
    {
        ArmaServerConfig armaServerConfig = prepareArmaServerConfig(List.of(MISSION_NAME_1));
        given(serverConfigStorage.getServerConfig()).willReturn(armaServerConfig);
        given(missionStorage.getInstalledMissionNames()).willReturn(List.of(MISSION_NAME_1, MISSION_NAME_2));

        Missions missions = missionService.getMissions();

        assertThat(missions.getDisabledMissions()).containsExactly(MISSION_2);
        assertThat(missions.getEnabledMissions()).containsExactly(MISSION_1);
    }

    @Test
    void shouldDeleteMission()
    {
        missionService.deleteMission(MISSION_NAME_1);

        verify(missionStorage, times(1)).deleteMission(MISSION_NAME_1);
    }

    private ArmaServerConfig prepareArmaServerConfig(List<String> missionNames)
    {
        ArmaServerConfig armaServerConfig = new ArmaServerConfig();
        ArmaServerConfig.Missions missions = new ArmaServerConfig.Missions();
        armaServerConfig.setMissions(missions);
        missions.setMissions(missionNames.stream().map(this::prepareMission).collect(Collectors.toList()));
        return armaServerConfig;
    }

    private ArmaServerConfig.Missions.Mission prepareMission(String missionName)
    {
        ArmaServerConfig.Missions.Mission mission = new ArmaServerConfig.Missions.Mission();
        mission.setTemplate(missionName);
        return mission;
    }
}