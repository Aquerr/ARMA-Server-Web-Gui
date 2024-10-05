package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("mod_settings")
@Data
@Builder
public class ModSettingsEntity
{
    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("active")
    private boolean active;
}
