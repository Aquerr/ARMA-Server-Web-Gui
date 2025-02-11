package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ModSettings extends ModSettingsHeader
{
    @Builder.Default
    private String content = "";
}
