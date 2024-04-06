package io.summarizeit.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

import static io.summarizeit.backend.util.Constants.PASSWORD_RESET_TOKEN_LENGTH;

@Entity
@Table(name = "password_reset_token")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetToken {
    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId("id")
    private User user;

    @Column(name = "token", nullable = false, length = PASSWORD_RESET_TOKEN_LENGTH, unique = true)
    private String token;

    @Column(name = "expiration_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationDate;
}
