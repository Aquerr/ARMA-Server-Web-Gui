package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ModSettingsHeader
{
    private Long id;
    private String name;
    private boolean active;
}
