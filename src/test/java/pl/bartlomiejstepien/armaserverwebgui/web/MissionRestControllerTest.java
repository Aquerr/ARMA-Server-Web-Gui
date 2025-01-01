package pl.bartlomiejstepien.armaserverwebgui.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import pl.bartlomiejstepien.armaserverwebgui.BaseIntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.interfaces.repository.MissionRepository;

import static pl.bartlomiejstepien.armaserverwebgui.TestUtils.loadJsonIntegrationContractFor;

class MissionRestControllerTest extends BaseIntegrationTest
{
    @Autowired
    private MissionRepository missionRepository;

    @BeforeEach
    public void setUp()
    {
        missionRepository.deleteAll().subscribe();
    }

    @Test
    void updateShouldReturnErrorWhenMissionDoesNotExist()
    {
        webTestClient.put()
                .uri("/api/v1/missions/id/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createUserAndJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loadJsonIntegrationContractFor("mission/update-mission.json"))
                .exchange()
                .expectStatus().isNotFound();
    }
}
