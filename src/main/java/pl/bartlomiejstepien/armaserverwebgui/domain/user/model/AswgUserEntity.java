package pl.bartlomiejstepien.armaserverwebgui.domain.user.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Data
@Table("aswg_user")
public class AswgUserEntity
{
    @Id
    @Column("id")
    private Integer id;

    @Column("username")
    private String username;

    @Column("password")
    private String password;

    @Column("locked")
    private boolean locked;

    @Column("created_date_time")
    private OffsetDateTime createdDateTime;
}
