package org.evernet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import org.evernet.enums.ActionType;
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
                @UniqueConstraint(columnNames = {
                        "nodeIdentifier",
                        "structureAddress",
                        "transitionIdentifier",
                        "actionParentReferenceChain",
                        "actionType",
                        "actionIdentifier"
                })
        }
)
public class TransitionAction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String nodeIdentifier;

    private String structureAddress;

    private String transitionIdentifier;

    private String actionParentReferenceChain;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    private String actionIdentifier;

    private String creator;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
