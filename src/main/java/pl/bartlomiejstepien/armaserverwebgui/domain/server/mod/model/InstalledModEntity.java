package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.nio.file.Paths;
import java.time.OffsetDateTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table("installed_mod")
public class InstalledModEntity
{
    @Id
    @Column("id")
    private Long id;

    @Column("workshop_file_id")
    private long workshopFileId;

    @Column("name")
    private String name;

    @Column("directory_path")
    private String directoryPath;

    @Column("preview_url")
    private String previewUrl;

    @Column("created_date")
    private OffsetDateTime createdDate;

    @Column("enabled")
    private boolean enabled;

    @Column("server_mod")
    private boolean serverMod;

    public String getModDirectoryName()
    {
        return Paths.get(directoryPath).getFileName().toString();
    }
}
