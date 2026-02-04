package org.evernet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
@Entity
@Table(name = "actors", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "signingPublicKey"
        }, name = "actor_signing_public_key"),
        @UniqueConstraint(columnNames = {
                "fingerprint"
        }, name = "actor_fingerprint")
})
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String fingerprint;

    private String signingPublicKey;

    private String displayName;

    private String type;

    private String description;

    private String alias;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private String encryptedSigningPrivateKey;

    private String creator;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
