package pl.bartlomiejstepien.armaserverwebgui.domain.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A mapping between user and authority
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_authority")
@Builder
@Entity
public class UserAuthorityEntity
{
    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Id
    @Column(name = "authority_id")
    private Integer authorityId;
}
