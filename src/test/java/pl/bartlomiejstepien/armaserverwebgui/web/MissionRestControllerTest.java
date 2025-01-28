package pl.bartlomiejstepien.armaserverwebgui.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import pl.bartlomiejstepien.armaserverwebgui.BaseIntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.model.MissionEntity;
import pl.bartlomiejstepien.armaserverwebgui.repository.MissionRepository;

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
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loadJsonIntegrationContractFor("mission/update-mission.json"))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldDeleteMissionReturnErrorWhenMissionNotFound() {
        webTestClient.delete()
                .uri("/api/v1/missions/template/test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldDeleteMission() {
        String missionTemplate = "1-operacja 1604.sehreno";
        missionRepository.save(new MissionEntity(null, "1604", missionTemplate, "custom", true, null)).block();

        String jwt = createJwtForTestUser();

        webTestClient.delete()
                .uri("/api/v1/missions/template/" + missionTemplate)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/api/v1/missions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("{\"disabledMissions\": [], \"enabledMissions\": []}");
    }
}
