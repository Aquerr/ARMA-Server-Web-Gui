package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Represents installed and managed mod by ASWG.
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "installed_mod")
@Entity
public class InstalledModEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "workshop_file_id")
    private long workshopFileId;

    @Column(name = "name")
    private String name;

    @Column(name = "directory_path")
    private String directoryPath;

    @Column(name = "preview_url")
    private String previewUrl;

    @Column(name = "created_date")
    private OffsetDateTime createdDate;

    @Column(name = "last_workshop_update_date")
    private OffsetDateTime lastWorkshopUpdateDate;

    @Column(name = "last_workshop_update_attempt_date")
    private OffsetDateTime lastWorkshopUpdateAttemptDate;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "server_mod")
    private boolean serverMod;

    @Column(name = "dependencies_ids", unique = false, nullable = false)
    @Convert(converter = ListOfLongsConverter.class)
    private List<Long> dependenciesIds;

    public String getModDirectoryName()
    {
        return Paths.get(directoryPath).getFileName().toString();
    }
}
