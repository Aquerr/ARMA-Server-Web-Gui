package pl.bartlomiejstepien.armaserverwebgui.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import pl.bartlomiejstepien.armaserverwebgui.BaseIntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

class ModsRestControllerTest extends BaseIntegrationTest
{
    @Autowired
    private ModService modService;

    @BeforeEach
    void setUp()
    {
        List<InstalledModEntity> modEntities = List.of(
                InstalledModEntity.builder()
                        .name("testmod")
                        .workshopFileId(123456789)
                        .directoryPath("./target/@testmod")
                        .createdDate(OffsetDateTime.now())
                        .build(),
                InstalledModEntity.builder()
                        .name("testmod2")
                        .workshopFileId(123456782)
                        .directoryPath("./target/@testmod2")
                        .createdDate(OffsetDateTime.now())
                        .build());

        Flux.fromIterable(modEntities)
                .map(InstalledModEntity::getName)
                .flatMap(modService::deleteMod)
                .subscribe();
    }

    @Test
    void uploadModFileShouldUploadFile()
    {
        // given
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new ClassPathResource("@testmod.zip"))
                .contentType(MediaType.MULTIPART_FORM_DATA);
        multipartBodyBuilder.part("file", new ClassPathResource("@testmod2.zip"))
                .contentType(MediaType.MULTIPART_FORM_DATA);

        // when
        WebTestClient.ResponseSpec responseSpec = webTestClient.mutate().responseTimeout(Duration.ofDays(1)).build().post()
                .uri("/api/v1/mods")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange();

        // then
        responseSpec.expectStatus().isOk();
    }
}