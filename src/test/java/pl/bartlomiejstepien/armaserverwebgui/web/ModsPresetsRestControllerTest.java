package pl.bartlomiejstepien.armaserverwebgui.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pl.bartlomiejstepien.armaserverwebgui.BaseIntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.TestUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModPresetService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.ModPresetEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.ModPresetSaveParams;
import pl.bartlomiejstepien.armaserverwebgui.repository.ModPresetEntryRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ModsPresetsRestControllerTest extends BaseIntegrationTest
{
    @Autowired
    private ModPresetService modPresetService;
    @Autowired
    private ModService modService;
    @MockitoSpyBean
    private ModPresetEntryRepository modPresetEntryRepository;
    @Captor
    private ArgumentCaptor<List<ModPresetEntity.EntryEntity>> modPresetEntryEntityArgumentCaptor;

    @Test
    void shouldGetModPresetsNames()
    {
        // given
        List<String> presetsNames = List.of("operation_false_brother", "Test_preset", "Attack_on_titan", "the_seeker");
        for (String presetName : presetsNames)
        {
            modPresetService.saveModPreset(ModPresetSaveParams.of(presetName, List.of()));
        }

        // when
        var response = getAuthenticatedRequest("/api/v1/mods-presets", ModsPresetsRestController.ModPresetNamesResponse.class);

        // then
        assertThat(response.getBody().getPresets()).containsExactly(
                "Attack_on_titan", "operation_false_brother", "Test_preset", "the_seeker"
        );

        // teardown
        for (String presetName : presetsNames)
        {
            modPresetService.deletePreset(presetName);
        }
    }

    @Test
    void shouldSelectPreset()
    {
        // given
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", new ClassPathResource("@testmod.zip"));
        parts.add("file", new ClassPathResource("@testmod2.zip"));

        // when
        var response1 = postAuthenticatedRequest("/api/v1/mods-files", parts, MediaType.MULTIPART_FORM_DATA_VALUE, Object.class);

        modPresetService.saveModPreset(ModPresetSaveParams.of("test-preset", List.of(
                "Test Mod"
        )));

        assertTrue(response1.getStatusCode().is2xxSuccessful());

        var response2 = postAuthenticatedRequest("/api/v1/mods-presets/select",
                TestUtils.loadJsonIntegrationContractFor("mods/select-mod-preset-request.json"),
                MediaType.APPLICATION_JSON_VALUE,
                Object.class);

        // then
        assertTrue(response2.getStatusCode().is2xxSuccessful());

        verify(modPresetEntryRepository).saveAll(modPresetEntryEntityArgumentCaptor.capture());
        assertThat(modPresetEntryEntityArgumentCaptor.getValue().getFirst()).satisfies(modPresetEntryEntity -> {
            assertThat(modPresetEntryEntity.getName()).isEqualTo("Test Mod");
        });

        List<InstalledModEntity> enabledMods = modService.getInstalledMods().stream().filter(InstalledModEntity::isEnabled).toList();
        assertThat(enabledMods).hasSize(1);
        assertThat(enabledMods.getFirst().getWorkshopFileId()).isEqualTo(123456789L);

        // tear down
        modService.getInstalledMods().stream()
                .map(InstalledModEntity::getName)
                .forEach(modService::deleteMod);
        modPresetService.deletePreset("test-preset");
    }
}