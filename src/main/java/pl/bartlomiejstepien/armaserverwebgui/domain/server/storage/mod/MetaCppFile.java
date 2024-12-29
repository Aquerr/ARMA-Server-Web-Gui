package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod;

import lombok.Data;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.annotation.CfgProperty;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.type.PropertyType;

@Data
public final class MetaCppFile implements CppFile
{
    @CfgProperty(name = "publishedid", type = PropertyType.LONG)
    private long publishedFileId;
    @CfgProperty(name = "name", type = PropertyType.QUOTED_STRING)
    private String name;
    @CfgProperty(name = "timestamp", type = PropertyType.LONG)
    private long timestamp;
}
