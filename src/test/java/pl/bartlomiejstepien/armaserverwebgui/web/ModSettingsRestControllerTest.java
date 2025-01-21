package pl.bartlomiejstepien.armaserverwebgui.web;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import pl.bartlomiejstepien.armaserverwebgui.BaseIntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.TestUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModSettingsService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModSettingsStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.ModSettings;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.ModSettingsEntity;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

class ModSettingsRestControllerTest extends BaseIntegrationTest
{
    @Autowired
    private ModSettingsService modSettingsService;

    @Autowired
    private ModSettingsStorage modSettingsStorage;

    @AfterEach
    void tearDown()
    {
        this.modSettingsService.getModSettingsWithoutContents()
                .flatMap(settings -> this.modSettingsService.deleteModSettings(settings.getId()))
                .subscribe();
    }

    @Test
    void shouldGetModSettings()
    {
        List<Long> ids = preSaveModSettings(ModSettings.builder()
                .name("testsettings")
                .active(false)
                .content("test-content")
                .build(),
                ModSettings.builder()
                .name("testsettingsactive")
                .active(true)
                .content("active-test-content")
                .build());

        WebTestClient.ResponseSpec responseSpec = webTestClient.get()
                .uri("/api/v1/mods/settings")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser())
                .exchange();

        responseSpec.expectStatus().isOk();
        responseSpec.expectBody().json(TestUtils.loadJsonIntegrationContractFor("mod-settings/get-mod-settings-list.json")
                .replace("{mod_settings_id_1}", Long.toString(ids.getFirst()))
                .replace("{mod_settings_id_2}", Long.toString(ids.get(1)))
        );
    }

    @Test
    void shouldGetSingleModSettings()
    {
        List<Long> ids = preSaveModSettings(ModSettings.builder()
                .name("new-mod-settings")
                .active(true)
                .content("new-content")
                .build());

        WebTestClient.ResponseSpec responseSpec = webTestClient.get()
                .uri("/api/v1/mods/settings/" + ids.getFirst())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser())
                .exchange();

        responseSpec.expectStatus().isOk();
        responseSpec.expectBody().json(TestUtils.loadJsonIntegrationContractFor("mod-settings/get-mod-settings.json")
                .replace("{mod_settings_id}", Long.toString(ids.getFirst())));
    }

    @Test
    void shouldGetModSettingsContent()
    {
        List<Long> ids = preSaveModSettings(ModSettings.builder()
                .name("new-mod-settings")
                .active(true)
                .content("new-content")
                .build());

        WebTestClient.ResponseSpec responseSpec = webTestClient.get()
                .uri("/api/v1/mods/settings/" + ids.getFirst())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser())
                .exchange();

        responseSpec.expectStatus().isOk();
        responseSpec.expectBody().json(TestUtils.loadJsonIntegrationContractFor("mod-settings/get-mod-settings.json")
                .replace("{mod_settings_id}", Long.toString(ids.getFirst())));
    }

    @Test
    void shouldUpdateModSettings()
    {
        List<Long> ids = preSaveModSettings(ModSettings.builder()
                .name("new-mod-settings")
                .active(true)
                .content("new-content")
                .build());

        WebTestClient.ResponseSpec responseSpec1 = webTestClient.put()
                .uri("/api/v1/mods/settings/" + ids.getFirst())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(ModSettings.builder()
                        .id(ids.getFirst())
                        .name("new-mod-settings-update")
                        .active(false)
                        .content("new-content-update")
                        .build()))
                .exchange();

        responseSpec1.expectStatus().isOk();
        responseSpec1.expectBody().json(TestUtils.loadJsonIntegrationContractFor("mod-settings/updated-mod-settings.json")
                .replace("{mod_settings_id}", Long.toString(ids.getFirst())));

        WebTestClient.ResponseSpec responseSpec2 = webTestClient.get()
                .uri("/api/v1/mods/settings/" + ids.getFirst() + "/content")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser())
                .exchange();

        responseSpec2.expectStatus().isOk();
        responseSpec2.expectBody().json(TestUtils.loadJsonIntegrationContractFor("mod-settings/updated-mod-settings-only-content.json"));
    }

    @Test
    void shouldCreateModSettings()
    {
        preSaveModSettings(ModSettings.builder()
                        .name("testsettings")
                        .active(false)
                        .content("test-content")
                        .build());

        WebTestClient.ResponseSpec responseSpec = webTestClient.get()
                .uri("/api/v1/mods/settings")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser())
                .exchange();

        responseSpec.expectStatus().isOk();
        responseSpec.expectBody().jsonPath("$.name", "testsettings");
    }

    @Test
    void shouldDeleteModSettings()
    {
        List<Long> ids = preSaveModSettings(ModSettings.builder()
                .name("testsettings")
                .active(false)
                .content("test-content")
                .build());

        WebTestClient.ResponseSpec responseSpec = webTestClient.delete()
                .uri("/api/v1/mods/settings/" + ids.getFirst())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser())
                .exchange();

        responseSpec.expectStatus().isOk();

        WebTestClient.ResponseSpec responseSpec2 = webTestClient.get()
                .uri("/api/v1/mods/settings")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser())
                .exchange();

        responseSpec2.expectStatus().isOk();
        responseSpec2.expectBody().json("[]");

        StepVerifier.create(modSettingsStorage.readModSettingsFileContent("testsettings", false))
                .expectNext("")
                .verifyComplete();
    }

    private List<Long> preSaveModSettings(ModSettings... modSettingsArray)
    {
        List<ModSettings> modSettingsList = Arrays.stream(modSettingsArray).toList();

        try
        {
            return Flux.fromIterable(modSettingsList)
                    .flatMap(modSettings -> this.modSettingsStorage.saveModSettingsFileContent(modSettings.getName(), modSettings.isActive(), modSettings.getContent())
                            .then(this.modSettingsStorage.save(ModSettingsEntity.builder()
                                    .name(modSettings.getName())
                                    .active(modSettings.isActive())
                                    .build())
                            ))
                    .map(ModSettingsEntity::getId)
                    .collectList()
                    .subscribeOn(Schedulers.immediate())
                    .toFuture().get();
        }
        catch (InterruptedException | ExecutionException e)
        {
            throw new RuntimeException(e);
        }
    }

    private WebTestClient.ResponseSpec postWithAuth(String uri, Object body)
    {
        return webTestClient.post()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(body))
                .exchange();
    }
}