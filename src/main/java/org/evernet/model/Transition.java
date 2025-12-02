package org.evernet.model;

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
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "nodeIdentifier",
                                "structureAddress",
                                "identifier"
                        }
                ),
                @UniqueConstraint(
                        columnNames = {
                                "nodeIdentifier",
                                "structureAddress",
                                "sourceStateIdentifier",
                                "eventParentReferenceChain",
                                "eventIdentifier"
                        }
                )

        }
)
public class Transition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String nodeIdentifier;

    private String structureAddress;

    private String sourceStateIdentifier;

    private String targetStateIdentifier;

    private String eventParentReferenceChain;

    private String eventIdentifier;

    private String identifier;

    private String displayName;

    private String description;

    private String creator;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
