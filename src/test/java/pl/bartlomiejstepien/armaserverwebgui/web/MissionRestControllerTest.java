package pl.bartlomiejstepien.armaserverwebgui.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
        var response = restTestClient.put().uri("/api/v1/missions/id/1")
                        .body(loadJsonIntegrationContractFor("mission/update-mission.json"))
                        .headers(headers -> headers.putAll(
                                MultiValueMap.fromSingleValue(Map.of(
                                        HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser(),
                                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                                        HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
                                ))
                        ))
                        .exchange()
                        .returnResult(String.class);

        assertThat(response.getStatus().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldDeleteMissionReturnErrorWhenMissionNotFound() {
        var response = restTestClient.method(HttpMethod.DELETE).uri("/api/v1/missions/template")
                        .body("{\"template\": \"test\"}")
                                .headers(headers -> headers.putAll(
                                        MultiValueMap.fromSingleValue(Map.of(
                                                HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser(),
                                                HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                                                HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
                                        ))
                                ))
                .exchange()
                .returnResult(String.class);

        assertThat(response.getStatus().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldDeleteMission() {
        String missionTemplate = "1-operacja 1604.sehreno";
        missionRepository.save(new MissionEntity(null, "1604", missionTemplate, "custom", true, null));

        String jwt = createJwtForTestUser();

        var response1 = restTestClient.method(HttpMethod.DELETE).uri("/api/v1/missions/template")
                        .body("{\"template\": \"" + missionTemplate + "\"}")
                                .headers(headers -> headers.putAll(
                                        MultiValueMap.fromSingleValue(Map.of(
                                                HttpHeaders.AUTHORIZATION, "Bearer " + jwt,
                                                HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                                                HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
                                        ))
                                ))
                .exchange()
                .returnResult(String.class);

        assertTrue(response1.getStatus().is2xxSuccessful());

        var response2 = getAuthenticatedRequest("/api/v1/missions", MissionRestController.GetMissionsResponse.class);

        assertTrue(response2.getStatusCode().is2xxSuccessful());
        assertThat(response2.getBody().getEnabledMissions()).isEmpty();
        assertThat(response2.getBody().getDisabledMissions()).isEmpty();
    }

    @Test
    void shouldDeleteMissionWithPercentageEncodedWhitespace() {
        String missionTemplate = "1-operacja%201604.sehreno";
        missionRepository.save(new MissionEntity(null, "1604", missionTemplate, "custom", true, null));

        String jwt = createJwtForTestUser();

        var response1 = restTestClient.method(HttpMethod.DELETE)
                .uri("/api/v1/missions/template")
                .body("{\"template\": \"" + missionTemplate + "\"}")
                        .headers(headers -> headers.putAll(
                                MultiValueMap.fromSingleValue(Map.of(
                                        HttpHeaders.AUTHORIZATION, "Bearer " + jwt,
                                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                                        HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
                                ))
                        ))
                .exchange()
                .returnResult(String.class);

        assertTrue(response1.getStatus().is2xxSuccessful());

        var response2 = getAuthenticatedRequest("/api/v1/missions", MissionRestController.GetMissionsResponse.class);

        assertTrue(response2.getStatusCode().is2xxSuccessful());
        assertThat(response2.getBody().getEnabledMissions()).isEmpty();
        assertThat(response2.getBody().getDisabledMissions()).isEmpty();
    }
}
