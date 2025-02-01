package pl.bartlomiejstepien.armaserverwebgui.web;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.json.JsonAssert;
import org.springframework.util.MultiValueMap;
import pl.bartlomiejstepien.armaserverwebgui.BaseIntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.general.model.GeneralProperties;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.general.GeneralService;
import pl.bartlomiejstepien.armaserverwebgui.web.response.GeneralPropertiesResponse;

import java.util.Map;

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
        given(generalService.getGeneralProperties()).willReturn(GeneralProperties.builder()
                .maxPlayers(MAX_PLAYERS)
                .build());

        var response = testRestTemplate.exchange(
                API_GENERAL_PROPERTIES_URL,
                HttpMethod.GET,
                new HttpEntity<>(null, MultiValueMap.fromSingleValue(Map.of(
                        HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser()
                ))),
                String.class
        );

        JSONAssert.assertEquals(
                loadJsonIntegrationContractFor("general/get-general-properties-response.json"),
                response.getBody(),
                JSONCompareMode.LENIENT);
    }

    @Test
    void getGeneralPropertiesShouldTriggerUnauthorizedErrorWhenUserNotAuthorized()
    {
        var response = testRestTemplate.exchange(
                API_GENERAL_PROPERTIES_URL,
                HttpMethod.GET,
                new HttpEntity<>(null),
                Object.class
        );

        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void saveGeneralPropertiesShouldUpdateServerAndModsDirectoryInASWGConfig()
    {
        String initialServerDirectory = aswgConfig.getServerDirectoryPath();
        String initialModsDirectory = aswgConfig.getModsDirectoryPath();

        var response = testRestTemplate.exchange(
                API_GENERAL_PROPERTIES_URL,
                HttpMethod.POST,
                new HttpEntity<>(loadJsonIntegrationContractFor("general/save-general-properties-request.json"), MultiValueMap.fromSingleValue(Map.of(
                        HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser(),
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE
                ))),
                GeneralPropertiesResponse.class
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());

        assertThat(aswgConfig.getServerDirectoryPath()).isEqualTo("fake/fakeServerDirectory");
        assertThat(aswgConfig.getModsDirectoryPath()).isEqualTo("anotherModsDirectory");
        verify(generalService).saveGeneralProperties(GeneralProperties.builder()
                .maxPlayers(MAX_PLAYERS)
                .build());

        aswgConfig.setServerDirectoryPath(initialServerDirectory);
        aswgConfig.setModsDirectoryPath(initialModsDirectory);
        aswgConfig.saveToFile();
    }

    @Test
    void saveGeneralPropertiesShouldTriggerUnauthorizedErrorWhenUserNotAuthorized()
    {
        var response = testRestTemplate.exchange(
                API_GENERAL_PROPERTIES_URL,
                HttpMethod.POST,
                new HttpEntity<>(loadJsonIntegrationContractFor("general/save-general-properties-request.json"), MultiValueMap.fromSingleValue(Map.of(
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE
                ))),
                Object.class
        );

        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}