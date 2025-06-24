package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.converter.MissionConverter;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto.Mission;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto.Missions;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.exception.MissionFileAlreadyExistsException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.model.MissionEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.ServerConfigStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mission.MissionFileNameHelper;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mission.MissionFileStorage;
import pl.bartlomiejstepien.armaserverwebgui.repository.MissionRepository;

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
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class MissionServiceImplTest
{
    private static final String MISSION_TEMPLATE_1 = "template1";
    private static final String MISSION_NAME_1 = "MyMission";
    private static final String MISSION_NAME_2 = "MySecondMission";

    @Mock
    private MissionFileStorage missionFileStorage;
    @Mock
    private ServerConfigStorage serverConfigStorage;
    @Mock
    private MissionRepository missionRepository;
    @Mock
    private MissionConverter missionConverter;
    @Mock
    private MissionFileNameHelper missionFileNameHelper;
    @InjectMocks
    private MissionServiceImpl missionService;

    @Captor
    private ArgumentCaptor<ArmaServerConfig> armaServerConfigArgumentCaptor;

    @Test
    void shouldSaveMissionFile() throws IOException
    {
        Mission mission = prepareMission(MISSION_NAME_1);
        mission.setEnabled(false);
        MissionEntity missionEntity = prepareMissionEntity(MISSION_NAME_1);
        MultipartFile multipartFile = mock(MultipartFile.class);
        given(multipartFile.getOriginalFilename()).willReturn(MISSION_NAME_1);
        given(missionFileStorage.doesMissionExists(MISSION_NAME_1)).willReturn(false);
        given(missionFileNameHelper.resolveMissionNameFromFilePart(multipartFile)).willReturn(MISSION_NAME_1);
        given(missionConverter.convertToEntity(mission)).willReturn(missionEntity);
        given(missionRepository.save(missionEntity)).willReturn(missionEntity);

        missionService.save(multipartFile, false);

        verify(missionFileStorage, times(1)).save(multipartFile);
    }

    @Test
    void shouldOverwriteMissionFile() throws IOException
    {
        Mission mission = prepareMission(MISSION_NAME_1);
        mission.setEnabled(false);
        MultipartFile multipartFile = mock(MultipartFile.class);
        given(missionFileNameHelper.resolveMissionNameFromFilePart(multipartFile)).willReturn(MISSION_NAME_1);

        missionService.save(multipartFile, true);

        verify(missionFileStorage, times(1)).save(multipartFile);
        verifyNoInteractions(missionRepository);
    }

    @Test
    void shouldThrowMissionFileAlreadyExistsExceptionWhenMissionFileExists()
    {
        MultipartFile multipartFile = mock(MultipartFile.class);
        given(multipartFile.getOriginalFilename()).willReturn(MISSION_NAME_1);
        given(missionFileStorage.doesMissionExists(MISSION_NAME_1)).willReturn(true);

        assertThrows(MissionFileAlreadyExistsException.class, () -> missionService.save(multipartFile, false));
    }

    @Test
    void shouldSaveEnabledMissionList()
    {
        MissionEntity missionEntity1 = prepareMissionEntity(MISSION_NAME_1);
        MissionEntity missionEntity2 = prepareMissionEntity(MISSION_NAME_2);
        Mission mission1 = prepareMission(MISSION_NAME_1);
        Mission mission2 = prepareMission(MISSION_NAME_2);
        List<Mission> missions = List.of(mission1, mission2);

        given(missionRepository.findAll()).willReturn(List.of(missionEntity1, missionEntity2));
        given(missionConverter.convertToDomainMission(missionEntity1)).willReturn(prepareMission(MISSION_NAME_1));
        given(missionConverter.convertToDomainMission(missionEntity2)).willReturn(prepareMission(MISSION_NAME_2));
        given(serverConfigStorage.getServerConfig()).willReturn(prepareArmaServerConfig(List.of(MISSION_NAME_1, MISSION_NAME_2)));
        given(missionConverter.convertToArmaMissionObject(mission1)).willReturn(prepareConfigMission(MISSION_NAME_1));
        given(missionConverter.convertToArmaMissionObject(mission2)).willReturn(prepareConfigMission(MISSION_NAME_2));
        given(serverConfigStorage.getServerConfig()).willReturn(prepareArmaServerConfig(List.of()));

        missionService.saveEnabledMissionList(missions);

        verify(missionRepository).disableAll();
        verify(missionRepository).updateAllByTemplateSetEnabled(List.of(MISSION_NAME_1, MISSION_NAME_2));
        verify(serverConfigStorage).saveServerConfig(armaServerConfigArgumentCaptor.capture());
        assertThat(armaServerConfigArgumentCaptor.getValue().getMissions())
                .extracting(ArmaServerConfig.Missions.Mission::getTemplate)
                .containsExactlyElementsOf(List.of(MISSION_NAME_1, MISSION_NAME_2));
    }

    @Test
    void shouldGetMissions()
    {
        MissionEntity missionEntity1 = prepareMissionEntity(MISSION_NAME_1);
        MissionEntity missionEntity2 = prepareMissionEntity(MISSION_NAME_2);
        missionEntity2.setEnabled(false);

        Mission mission1 = prepareMission(MISSION_NAME_1);
        Mission mission2 = prepareMission(MISSION_NAME_2);
        mission2.setEnabled(false);
        given(missionRepository.findAll()).willReturn(List.of(missionEntity1, missionEntity2));
        given(missionConverter.convertToDomainMission(missionEntity1)).willReturn(mission1);
        given(missionConverter.convertToDomainMission(missionEntity2)).willReturn(mission2);

        Missions missions = missionService.getMissions();

        assertThat(missions.getDisabledMissions()).containsExactly(mission2);
        assertThat(missions.getEnabledMissions()).containsExactly(mission1);
    }

    @Test
    void shouldDeleteMission()
    {
        MissionEntity missionEntity1 = prepareMissionEntity(MISSION_NAME_2);
        given(missionRepository.findByTemplate(MISSION_TEMPLATE_1)).willReturn(List.of(missionEntity1));

        missionService.deleteMission(MISSION_TEMPLATE_1);

        verify(missionRepository).deleteFirstByTemplate(MISSION_TEMPLATE_1);
        verify(missionFileStorage, times(1)).deleteMission(MISSION_TEMPLATE_1);
    }

    private Mission prepareMission(String missionTemplate)
    {
        return Mission.builder()
                .name(missionTemplate)
                .template(missionTemplate)
                .enabled(true)
                .difficulty(Mission.Difficulty.REGULAR)
                .parameters(Collections.emptySet())
                .build();
    }

    private MissionEntity prepareMissionEntity(String missionTemplate)
    {
        MissionEntity entity = new MissionEntity();
        entity.setName(missionTemplate);
        entity.setTemplate(missionTemplate);
        entity.setEnabled(true);
        entity.setDifficulty("REGULAR");
        return entity;
    }

    private ArmaServerConfig prepareArmaServerConfig(List<String> missionNames)
    {
        ArmaServerConfig armaServerConfig = new ArmaServerConfig();
        armaServerConfig.setMissions(missionNames.stream().map(this::prepareConfigMission).collect(Collectors.toList()));
        return armaServerConfig;
    }

    private ArmaServerConfig.Missions.Mission prepareConfigMission(String missionName)
    {
        ArmaServerConfig.Missions.Mission mission = new ArmaServerConfig.Missions.Mission();
        mission.setTemplate(missionName);
        mission.setDifficulty("REGULAR");
        return mission;
    }
}