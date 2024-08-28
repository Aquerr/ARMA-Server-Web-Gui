package pl.bartlomiejstepien.armaserverwebgui.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import pl.bartlomiejstepien.armaserverwebgui.BaseIntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.logging.model.LoggingProperties;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.logging.LoggingService;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static pl.bartlomiejstepien.armaserverwebgui.TestUtils.loadJsonIntegrationContractFor;

class LoggingRestControllerTest extends BaseIntegrationTest
{
    private static final String LOG_FILE_PATH = "log/logFilePath";
    private static final String LOGGING_PROPERTIES_URL = "/api/v1/logging/properties";

    @MockBean
    private LoggingService loggingService;

    @Test
    void getLoggingPropertiesShouldReturnLoggingSectionData()
    {
        given(loggingService.getLoggingProperties()).willReturn(prepareLoggingProperties());

        webTestClient.get()
                .uri(LOGGING_PROPERTIES_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createJwt("test_user"))
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(loadJsonIntegrationContractFor("logging/get-logging-properties.json"));
    }

    @Test
    void getLoggingPropertiesShouldTriggerUnauthorizedErrorWhenUserNotAuthorized()
    {
        webTestClient.get()
                .uri(LOGGING_PROPERTIES_URL)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void saveLoggingPropertiesShouldSavePropertiesUsingService()
    {
        webTestClient.post()
                .uri(LOGGING_PROPERTIES_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createJwt("test_user"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loadJsonIntegrationContractFor("logging/save-logging-properties.json"))
                .exchange()
                .expectStatus()
                .isOk();

        verify(loggingService).saveLoggingProperties(LoggingProperties.builder()
                .logFile(LOG_FILE_PATH)
                .build());
    }

    @Test
    void saveLoggingPropertiesShouldTriggerUnauthorizedErrorWhenUserNotAuthorized()
    {
        webTestClient.post()
                .uri(LOGGING_PROPERTIES_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loadJsonIntegrationContractFor("logging/save-logging-properties.json"))
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    private LoggingProperties prepareLoggingProperties()
    {
        return LoggingProperties.builder()
                .logFile(LOG_FILE_PATH)
                .build();
    }
}