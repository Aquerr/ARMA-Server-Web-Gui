package pl.bartlomiejstepien.armaserverwebgui.application.security.jwt.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.ZonedDateTime;

@Data
@Table("invalid_jwt_token")
public class InvalidJwtTokenEntity
{
    @Id
    @Column("id")
    private Integer id;

    @Column("jwt")
    private String jwt;

    @Column("invalidated_date_time")
    private ZonedDateTime invalidatedDateTime;

    @Column("expiration_date_time")
    private ZonedDateTime expirationDateTime;
}
