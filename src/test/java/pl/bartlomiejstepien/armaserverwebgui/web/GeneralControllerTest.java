package pl.bartlomiejstepien.armaserverwebgui.web;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import pl.bartlomiejstepien.armaserverwebgui.BaseIntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.general.model.GeneralProperties;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.general.GeneralService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto.Mission;
import pl.bartlomiejstepien.armaserverwebgui.web.response.GeneralPropertiesResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static pl.bartlomiejstepien.armaserverwebgui.TestUtils.loadJsonIntegrationContractFor;

class GeneralControllerTest extends BaseIntegrationTest
{
    private static final int MAX_PLAYERS = 10;
    private static final String API_GENERAL_PROPERTIES_URL = "/api/v1/general/properties";

    @MockitoBean
    private GeneralService generalService;

    @Autowired
    private ASWGConfig aswgConfig;

    @Test
    void getGeneralPropertiesShouldReturnServerDirectoryFromASWGConfig() throws JSONException
    {
        aswgConfig.setServerDirectoryPath("./target");
        aswgConfig.setModsDirectoryPath("mods");
        given(generalService.getGeneralProperties()).willReturn(GeneralProperties.builder()
                .maxPlayers(MAX_PLAYERS)
                .build());

        var response = getAuthenticatedRequest(API_GENERAL_PROPERTIES_URL, String.class);

        JSONAssert.assertEquals(
                loadJsonIntegrationContractFor("general/get-general-properties-response.json"),
                response.getBody(),
                JSONCompareMode.LENIENT);
    }

    @Test
    void getGeneralPropertiesShouldTriggerForbiddenErrorWhenUserNotAuthorized()
    {
        var response = restTestClient.get().uri(API_GENERAL_PROPERTIES_URL).exchange().returnResult();
        assertThat(response.getStatus().value()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void saveGeneralPropertiesShouldUpdateServerAndModsDirectoryInASWGConfig()
    {
        String initialServerDirectory = aswgConfig.getServerDirectoryPath();
        String initialModsDirectory = aswgConfig.getModsDirectoryPath();

        var response = postAuthenticatedRequest(
                API_GENERAL_PROPERTIES_URL,
                loadJsonIntegrationContractFor("general/save-general-properties-request.json"),
                MediaType.APPLICATION_JSON_VALUE,
                GeneralPropertiesResponse.class
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());

        assertThat(aswgConfig.getServerDirectoryPath()).isEqualTo("fake/fakeServerDirectory");
        assertThat(aswgConfig.getModsDirectoryPath()).isEqualTo("anotherModsDirectory");
        verify(generalService).saveGeneralProperties(GeneralProperties.builder()
                .maxPlayers(MAX_PLAYERS)
                .forcedDifficulty(Mission.Difficulty.RECRUIT)
                .build());

        aswgConfig.setServerDirectoryPath(initialServerDirectory);
        aswgConfig.setModsDirectoryPath(initialModsDirectory);
        aswgConfig.saveToFile();
    }

    @Test
    void saveGeneralPropertiesShouldTriggerForbiddenErrorWhenUserNotAuthorized()
    {
        var response = restTestClient.post().uri(API_GENERAL_PROPERTIES_URL)
                .body(loadJsonIntegrationContractFor("general/save-general-properties-request.json"))
                .exchange()
                .returnResult(Object.class);

        assertThat(response.getStatus().value()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }
}