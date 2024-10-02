package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("mission")
public class MissionEntity
{
    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Column("template")
    private String template;

    @Column("difficulty")
    private String difficulty;

    @Column("enabled")
    private boolean enabled;

    @Column("parameters")
    private String parametersJson;
}
