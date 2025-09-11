package pl.bartlomiejstepien.armaserverwebgui.web.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CdlcApiModel
{
    private Long id;
    private String name;
    private boolean enabled;
    private boolean fileExists;
}
