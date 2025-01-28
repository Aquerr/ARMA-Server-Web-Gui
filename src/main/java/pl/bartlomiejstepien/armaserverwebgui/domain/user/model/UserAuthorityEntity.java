package pl.bartlomiejstepien.armaserverwebgui.domain.user.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A mapping between user and authority
 */
@Data
@Table("user_authority")
@Builder
public class UserAuthorityEntity
{
    @Column("user_id")
    private Integer userId;

    @Column("authority_id")
    private Integer authorityId;
}
