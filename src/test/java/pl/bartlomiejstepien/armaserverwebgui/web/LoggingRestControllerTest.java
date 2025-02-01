package pl.bartlomiejstepien.armaserverwebgui.web;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.MultiValueMap;
import pl.bartlomiejstepien.armaserverwebgui.BaseIntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.logging.model.LoggingProperties;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.logging.LoggingService;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static pl.bartlomiejstepien.armaserverwebgui.TestUtils.loadJsonIntegrationContractFor;

class LoggingRestControllerTest extends BaseIntegrationTest
{
    private static final String LOG_FILE_PATH = "log/logFilePath";
    private static final String LOGGING_PROPERTIES_URL = "/api/v1/logging/properties";

    @MockitoBean
    private LoggingService loggingService;

    @Test
    void getLoggingPropertiesShouldReturnLoggingSectionData() throws JSONException
    {
        // given
        given(loggingService.getLoggingProperties()).willReturn(prepareLoggingProperties());

        // when
        // then
        var response = testRestTemplate.exchange(
                LOGGING_PROPERTIES_URL,
                HttpMethod.GET,
                new HttpEntity<>(null, MultiValueMap.fromSingleValue(Map.of(
                        HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser(),
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                        HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
                ))),
                String.class
        );

        JSONAssert.assertEquals(loadJsonIntegrationContractFor("logging/get-logging-properties.json"), response.getBody(), JSONCompareMode.LENIENT);
    }

    @Test
    void getLoggingPropertiesShouldTriggerUnauthorizedErrorWhenUserNotAuthorized()
    {
        var response = testRestTemplate.exchange(
                LOGGING_PROPERTIES_URL,
                HttpMethod.GET,
                new HttpEntity<>(MultiValueMap.fromSingleValue(Map.of(
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                        HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
                ))),
                String.class
        );

        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void saveLoggingPropertiesShouldSavePropertiesUsingService()
    {
        var response = testRestTemplate.exchange(
                LOGGING_PROPERTIES_URL,
                HttpMethod.POST,
                new HttpEntity<>(loadJsonIntegrationContractFor("logging/save-logging-properties.json"), MultiValueMap.fromSingleValue(Map.of(
                        HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser(),
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                        HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
                ))),
                String.class
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(loggingService).saveLoggingProperties(LoggingProperties.builder()
                .logFile(LOG_FILE_PATH)
                .build());
    }

    @Test
    void saveLoggingPropertiesShouldTriggerUnauthorizedErrorWhenUserNotAuthorized()
    {
        var response = testRestTemplate.exchange(
                LOGGING_PROPERTIES_URL,
                HttpMethod.POST,
                new HttpEntity<>(loadJsonIntegrationContractFor("logging/save-logging-properties.json"), MultiValueMap.fromSingleValue(Map.of(
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                        HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
                ))),
                String.class
        );

        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    private LoggingProperties prepareLoggingProperties()
    {
        return LoggingProperties.builder()
                .logFile(LOG_FILE_PATH)
                .build();
    }
}