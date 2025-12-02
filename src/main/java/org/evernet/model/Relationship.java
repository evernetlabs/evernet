package org.evernet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import org.evernet.enums.RelationshipType;
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
                        "nodeIdentifier",
                        "fromStructureAddress",
                        "identifier"
                })
        }
)
@Entity
public class Relationship {

    @Id
    private String id;

    private String nodeIdentifier;

    private String fromStructureAddress;

    private String toStructureAddress;

    @Enumerated(EnumType.STRING)
    private RelationshipType type;

    private String identifier;

    private String displayName;

    private String description;

    private String creator;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
