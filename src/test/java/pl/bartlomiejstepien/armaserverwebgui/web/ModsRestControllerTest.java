package pl.bartlomiejstepien.armaserverwebgui.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import pl.bartlomiejstepien.armaserverwebgui.IntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.application.config.security.JwtService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModService;
import pl.bartlomiejstepien.armaserverwebgui.web.validator.ModFileValidator;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@IntegrationTest
class ModsRestControllerTest
{
    @Autowired
    private JwtService jwtService;
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private ModFileValidator validator;

    @MockBean
    private ModService modService;

    @Test
    void uploadModFileShouldUploadFile()
    {
        // given
        given(modService.saveModFile(any())).willReturn(Mono.empty());

        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new ClassPathResource("application.zip"))
                .contentType(MediaType.MULTIPART_FORM_DATA);
        multipartBodyBuilder.part("file", new ClassPathResource("test.zip"))
                .contentType(MediaType.MULTIPART_FORM_DATA);

        // when
        WebTestClient.ResponseSpec responseSpec = webTestClient.post()
                .uri("/api/v1/mods")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createJwt("test_user"))
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange();

        // then
        System.out.println("test");
        responseSpec.expectStatus().isOk();
    }
}