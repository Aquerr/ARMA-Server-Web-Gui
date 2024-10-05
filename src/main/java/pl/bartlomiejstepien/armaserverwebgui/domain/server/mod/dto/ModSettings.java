package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModSettings
{
    private Long id;
    private String name;
    private boolean active;
}
