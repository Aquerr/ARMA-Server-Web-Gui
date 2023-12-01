package pl.bartlomiejstepien.armaserverwebgui.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModView
{
    private Long workshopFileId;
    private String name;
    private boolean serverMod;
    private String previewUrl;
    private String workshopUrl;
    private boolean fileExists;
}
