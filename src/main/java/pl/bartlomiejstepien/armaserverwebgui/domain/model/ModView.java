package pl.bartlomiejstepien.armaserverwebgui.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModView
{
    private String name;
    private boolean serverMod;
    private String previewUrl;
    private String workshopUrl;
}
