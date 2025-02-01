package pl.bartlomiejstepien.armaserverwebgui.application.security.jwt.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Table(name = "invalid_jwt_token")
@Entity
public class InvalidJwtTokenEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "jwt")
    private String jwt;

    @Column(name = "invalidated_date_time")
    private ZonedDateTime invalidatedDateTime;

    @Column(name = "expiration_date_time")
    private ZonedDateTime expirationDateTime;
}
