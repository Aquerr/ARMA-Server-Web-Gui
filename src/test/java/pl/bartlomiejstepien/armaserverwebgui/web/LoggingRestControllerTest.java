package pl.bartlomiejstepien.armaserverwebgui.web;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import pl.bartlomiejstepien.armaserverwebgui.BaseIntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.logging.model.LoggingProperties;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.logging.LoggingService;

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
        var response = getAuthenticatedRequest(LOGGING_PROPERTIES_URL);

        // then
        JSONAssert.assertEquals(loadJsonIntegrationContractFor("logging/get-logging-properties.json"), response.getBody(), JSONCompareMode.LENIENT);
    }

    @Test
    void getLoggingPropertiesShouldTriggerForbiddenErrorWhenUserNotAuthorized()
    {
        var response = restTestClient.get()
                .uri(LOGGING_PROPERTIES_URL)
                .exchange()
                .returnResult(String.class);

        assertThat(response.getStatus().value()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void saveLoggingPropertiesShouldSavePropertiesUsingService()
    {
        var response = postAuthenticatedRequest(LOGGING_PROPERTIES_URL, loadJsonIntegrationContractFor("logging/save-logging-properties.json"));

        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(loggingService).saveLoggingProperties(LoggingProperties.builder()
                .logFile(LOG_FILE_PATH)
                .build());
    }

    @Test
    void saveLoggingPropertiesShouldTriggerForbiddenErrorWhenUserNotAuthorized()
    {
        var response = restTestClient.post()
                .uri(LOGGING_PROPERTIES_URL)
                .body(loadJsonIntegrationContractFor("logging/save-logging-properties.json"))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .returnResult(String.class);

        assertThat(response.getStatus().value()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    private LoggingProperties prepareLoggingProperties()
    {
        return LoggingProperties.builder()
                .logFile(LOG_FILE_PATH)
                .build();
    }
}