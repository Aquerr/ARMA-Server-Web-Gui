package pl.bartlomiejstepien.armaserverwebgui.web;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import pl.bartlomiejstepien.armaserverwebgui.BaseIntegrationTest;

import static pl.bartlomiejstepien.armaserverwebgui.TestUtils.loadJsonIntegrationContractFor;

class MissionRestControllerTest extends BaseIntegrationTest
{
    @Test
    void updateShouldReturnErrorWhenMissionDoesNotExist()
    {
        webTestClient.put()
                .uri("/api/v1/missions/id/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createJwt("test_user"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loadJsonIntegrationContractFor("mission/update-mission.json"))
                .exchange()
                .expectStatus().isNotFound();
    }
}
