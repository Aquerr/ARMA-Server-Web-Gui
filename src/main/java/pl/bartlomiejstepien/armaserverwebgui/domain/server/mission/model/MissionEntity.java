package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mission")
@Entity
public class MissionEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "template")
    private String template;

    @Column(name = "difficulty")
    private String difficulty;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "parameters", columnDefinition = "CHARACTER LARGE OBJECT")
    private String parametersJson;
}
