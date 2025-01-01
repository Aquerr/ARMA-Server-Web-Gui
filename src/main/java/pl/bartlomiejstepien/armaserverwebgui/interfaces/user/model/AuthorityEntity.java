package pl.bartlomiejstepien.armaserverwebgui.interfaces.user.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("aswg_authority")
public class AuthorityEntity
{
    @Id
    @Column("id")
    private Integer id;

    @Column("code")
    private String code;
}
