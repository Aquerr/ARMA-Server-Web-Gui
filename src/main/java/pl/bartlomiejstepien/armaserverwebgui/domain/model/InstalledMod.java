package pl.bartlomiejstepien.armaserverwebgui.domain.model;

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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("installed_mod")
public class InstalledMod
{
    @Id
    @Column("id")
    private Long id;

    @Column("workshop_file_id")
    private long publishedFileId;

    @Column("name")
    private String name;

    @Column("directory_path")
    private String directoryPath;

    @Column("preview_url")
    private String previewUrl;

    @Column("created_date")
    private OffsetDateTime createdDate;

    public String getModDirectoryName()
    {
        return Paths.get(directoryPath).getFileName().toString();
    }
}
