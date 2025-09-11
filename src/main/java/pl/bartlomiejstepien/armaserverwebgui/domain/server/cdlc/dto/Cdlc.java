package pl.bartlomiejstepien.armaserverwebgui.domain.server.cdlc.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Cdlc
{
    private Long id;
    private String name;
    private String directoryName;
    private boolean enabled;
    private boolean fileExists;
}
