package pl.bartlomiejstepien.armaserverwebgui.web;

import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.util.MultiValueMap;
import pl.bartlomiejstepien.armaserverwebgui.BaseIntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.TestUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModSettingsService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModSettingsStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.ModSettings;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.ModSettingsHeader;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.ModSettingsEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModSettingsRestControllerTest extends BaseIntegrationTest
{
    @Autowired
    private ModSettingsService modSettingsService;

    @MockitoSpyBean
    private ModSettingsStorage modSettingsStorage;

    @AfterEach
    void tearDown()
    {
        this.modSettingsService.getModSettingsWithoutContents().stream()
                .map(ModSettingsHeader::getId)
                .forEach(this.modSettingsService::deleteModSettings);
    }

    @Test
    void shouldGetModSettings() throws JSONException
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

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "/api/v1/mods/settings",
                HttpMethod.GET,
                new HttpEntity<>(MultiValueMap.fromSingleValue(Map.of(
                        HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser(),
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                        HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
                ))), String.class);

        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        JSONAssert.assertEquals(TestUtils.loadJsonIntegrationContractFor("mod-settings/get-mod-settings-list.json")
                .replace("{mod_settings_id_1}", Long.toString(ids.getFirst()))
                .replace("{mod_settings_id_2}", Long.toString(ids.get(1))), responseEntity.getBody(), JSONCompareMode.LENIENT);
    }

    @Test
    void shouldGetSingleModSettings() throws JSONException
    {
        List<Long> ids = preSaveModSettings(ModSettings.builder()
                .name("new-mod-settings")
                .active(true)
                .content("new-content")
                .build());

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "/api/v1/mods/settings/" + ids.getFirst(),
                        HttpMethod.GET,
                        new HttpEntity<>(MultiValueMap.fromSingleValue(Map.of(
                                HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser(),
                                HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                                HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
                        ))), String.class);

        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        JSONAssert.assertEquals(TestUtils.loadJsonIntegrationContractFor("mod-settings/get-mod-settings.json")
                .replace("{mod_settings_id}", Long.toString(ids.getFirst())), responseEntity.getBody(), JSONCompareMode.LENIENT);
    }

    @Test
    void shouldGetModSettingsContent() throws JSONException
    {
        List<Long> ids = preSaveModSettings(ModSettings.builder()
                .name("new-mod-settings")
                .active(true)
                .content("new-content")
                .build());

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "/api/v1/mods/settings/" + ids.getFirst(),
                        HttpMethod.GET,
                        new HttpEntity<>(MultiValueMap.fromSingleValue(Map.of(
                                HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser(),
                                HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                                HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
                        ))), String.class);

        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        JSONAssert.assertEquals(TestUtils.loadJsonIntegrationContractFor("mod-settings/get-mod-settings.json")
                .replace("{mod_settings_id}", Long.toString(ids.getFirst())), responseEntity.getBody(), JSONCompareMode.LENIENT);
    }

    @Test
    void shouldUpdateModSettings() throws JSONException
    {
        List<Long> ids = preSaveModSettings(ModSettings.builder()
                .name("new-mod-settings")
                .active(true)
                .content("new-content")
                .build());

        ResponseEntity<String> responseEntity = testRestTemplate.exchange("/api/v1/mods/settings/" + ids.getFirst(),
                        HttpMethod.PUT,
                        new HttpEntity<>(ModSettings.builder()
                                .id(ids.getFirst())
                                .name("new-mod-settings-update")
                                .active(false)
                                .content("new-content-update")
                                .build(), MultiValueMap.fromSingleValue(Map.of(
                                HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser(),
                                HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                                HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
                        ))), String.class);

        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        JSONAssert.assertEquals(TestUtils.loadJsonIntegrationContractFor("mod-settings/updated-mod-settings.json")
                .replace("{mod_settings_id}", Long.toString(ids.getFirst())), responseEntity.getBody(), JSONCompareMode.LENIENT);

        ResponseEntity<String> responseEntity2 = testRestTemplate.exchange("/api/v1/mods/settings/" + ids.getFirst() + "/content",
                HttpMethod.GET,
                new HttpEntity<>(MultiValueMap.fromSingleValue(Map.of(
                        HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser(),
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                        HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
                ))),
                String.class);

        assertTrue(responseEntity2.getStatusCode().is2xxSuccessful());
        JSONAssert.assertEquals(TestUtils.loadJsonIntegrationContractFor("mod-settings/updated-mod-settings-only-content.json"), responseEntity2.getBody(), JSONCompareMode.LENIENT);
    }

    @Test
    void shouldCreateModSettings()
    {
        preSaveModSettings(ModSettings.builder()
                        .name("testsettings")
                        .active(false)
                        .content("test-content")
                        .build());

        ResponseEntity<List<ModSettingsHeader>> responseEntity = testRestTemplate.exchange(
                "/api/v1/mods/settings",
                HttpMethod.GET,
                new HttpEntity<>(MultiValueMap.fromSingleValue(Map.of(
                        HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser(),
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                        HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
                ))), new ParameterizedTypeReference<>() {});

        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertThat(responseEntity.getBody()).hasSize(1);
        assertThat(responseEntity.getBody().get(0).getName()).isEqualTo("testsettings");
    }

    @Test
    void shouldDeleteModSettings()
    {
        List<Long> ids = preSaveModSettings(ModSettings.builder()
                .name("testsettings")
                .active(false)
                .content("test-content")
                .build());

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "/api/v1/mods/settings/" + ids.getFirst(),
                    HttpMethod.DELETE,
                    new HttpEntity<>(MultiValueMap.fromSingleValue(Map.of(
                            HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser(),
                            HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                            HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
                    ))), String.class);

        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());

        ResponseEntity<List> responseEntity2 = testRestTemplate.exchange(
            "/api/v1/mods/settings",
                        HttpMethod.GET,
                        new HttpEntity<>(MultiValueMap.fromSingleValue(Map.of(
                                HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser(),
                                HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                                HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
                        ))), List.class);

        assertTrue(responseEntity2.getStatusCode().is2xxSuccessful());
        assertThat(responseEntity2.getBody()).isEmpty();
    }

    private List<Long> preSaveModSettings(ModSettings... modSettingsArray)
    {
        return Arrays.stream(modSettingsArray)
                .map(modSettings -> {
                    modSettingsStorage.saveModSettingsFileContent(modSettings.getName(), modSettings.isActive(), modSettings.getContent());
                    return modSettingsStorage.save(ModSettingsEntity.builder()
                            .name(modSettings.getName())
                            .active(modSettings.isActive())
                            .build());
                })
                .map(ModSettingsEntity::getId)
                .toList();
    }
}