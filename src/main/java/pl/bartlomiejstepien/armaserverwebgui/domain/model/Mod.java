package pl.bartlomiejstepien.armaserverwebgui.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Mod
{
    private Long workshopFileId;
    private String name;
    private boolean serverMod;
    private String previewUrl;
    private String workshopUrl;
    private ModStatus status;
    private long sizeBytes;
    private String directoryName;
    private List<Long> dependenciesIds;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private OffsetDateTime lastWorkshopUpdateDateTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private OffsetDateTime lastWorkshopUpdateAttemptDateTime;
}
