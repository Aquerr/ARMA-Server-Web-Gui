package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod;

import lombok.Data;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.annotation.CfgProperty;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.type.PropertyType;

@Data
public class ModCppFile implements CppFile
{
    @CfgProperty(name = "name", type = PropertyType.RAW_STRING)
    private String name;
}
