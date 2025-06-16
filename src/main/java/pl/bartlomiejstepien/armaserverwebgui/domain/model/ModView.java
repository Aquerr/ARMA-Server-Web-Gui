package pl.bartlomiejstepien.armaserverwebgui.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Data
@SuperBuilder
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
    private long sizeBytes;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private OffsetDateTime lastWorkshopUpdateDateTime;
}
