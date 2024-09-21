package pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("difficulty_profile")
public class DifficultyProfileEntity
{
    @Id
    @Column("id")
    private Integer id;

    @Column("name")
    private String name;

    @Column("active")
    private boolean active;
}
