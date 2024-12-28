package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.annotation.CfgProperty;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.type.PropertyType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KickTimeout
{
    @CfgProperty(name = "kickId", type = PropertyType.INTEGER)
    private int kickId;

    @CfgProperty(name = "timeout", type = PropertyType.INTEGER)
    private int timeout;
}
