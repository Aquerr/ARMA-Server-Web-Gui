package pl.bartlomiejstepien.armaserverwebgui.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import pl.bartlomiejstepien.armaserverwebgui.BaseIntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.model.MissionEntity;
import pl.bartlomiejstepien.armaserverwebgui.repository.MissionRepository;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.bartlomiejstepien.armaserverwebgui.TestUtils.loadJsonIntegrationContractFor;

class MissionRestControllerTest extends BaseIntegrationTest
{
    @Autowired
    private MissionRepository missionRepository;

    @BeforeEach
    public void setUp()
    {
        missionRepository.deleteAll();
    }

    @Test
    void updateShouldReturnErrorWhenMissionDoesNotExist()
    {
        var response = testRestTemplate.exchange(
                "/api/v1/missions/id/1",
                HttpMethod.PUT,
                new HttpEntity<>(loadJsonIntegrationContractFor("mission/update-mission.json"), MultiValueMap.fromSingleValue(Map.of(
                        HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser(),
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                        HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
                ))),
                String.class
        );

        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldDeleteMissionReturnErrorWhenMissionNotFound() {
        var response = testRestTemplate.exchange(
                "/api/v1/missions/template",
                HttpMethod.DELETE,
                new HttpEntity<>("{\"template\": \"test\"}", MultiValueMap.fromSingleValue(Map.of(
                        HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser(),
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                        HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
                ))),
                String.class
        );

        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldDeleteMission() {
        String missionTemplate = "1-operacja 1604.sehreno";
        missionRepository.save(new MissionEntity(null, "1604", missionTemplate, "custom", true, null));

        String jwt = createJwtForTestUser();

        var response1 = testRestTemplate.exchange(
                "/api/v1/missions/template",
                HttpMethod.DELETE,
                new HttpEntity<>("{\"template\": \"" + missionTemplate + "\"}", MultiValueMap.fromSingleValue(Map.of(
                        HttpHeaders.AUTHORIZATION, "Bearer " + jwt,
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                        HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
                ))),
                String.class
        );
        assertTrue(response1.getStatusCode().is2xxSuccessful());

        var response2 = testRestTemplate.exchange(
                "/api/v1/missions",
                HttpMethod.GET,
                new HttpEntity<>(null, MultiValueMap.fromSingleValue(Map.of(
                        HttpHeaders.AUTHORIZATION, "Bearer " + jwt,
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                        HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
                ))),
                MissionRestController.GetMissionsResponse.class
        );

        assertTrue(response2.getStatusCode().is2xxSuccessful());
        assertThat(response2.getBody().getEnabledMissions()).isEmpty();
        assertThat(response2.getBody().getDisabledMissions()).isEmpty();
    }

    @Test
    void shouldDeleteMissionWithPercentageEncodedWhitespace() {
        String missionTemplate = "1-operacja%201604.sehreno";
        missionRepository.save(new MissionEntity(null, "1604", missionTemplate, "custom", true, null));

        String jwt = createJwtForTestUser();

        var response1 = testRestTemplate.exchange(
                "/api/v1/missions/template",
                HttpMethod.DELETE,
                new HttpEntity<>("{\"template\": \"" + missionTemplate + "\"}", MultiValueMap.fromSingleValue(Map.of(
                        HttpHeaders.AUTHORIZATION, "Bearer " + jwt,
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                        HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
                ))),
                String.class
        );
        assertTrue(response1.getStatusCode().is2xxSuccessful());

        var response2 = testRestTemplate.exchange(
                "/api/v1/missions",
                HttpMethod.GET,
                new HttpEntity<>(null, MultiValueMap.fromSingleValue(Map.of(
                        HttpHeaders.AUTHORIZATION, "Bearer " + jwt,
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                        HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
                ))),
                MissionRestController.GetMissionsResponse.class
        );

        assertTrue(response2.getStatusCode().is2xxSuccessful());
        assertThat(response2.getBody().getEnabledMissions()).isEmpty();
        assertThat(response2.getBody().getDisabledMissions()).isEmpty();
    }
}
