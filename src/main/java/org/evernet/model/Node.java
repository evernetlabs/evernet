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
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "identifier"
                }, name = "node_identifier")
        }
)
@Entity
public class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String identifier;

    private String displayName;

    private String description;

    private Boolean open;

    @JsonIgnore
    private String signingPrivateKey;

    private String signingPublicKey;

    private String creator;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
