package org.evernet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import org.evernet.enums.DataFormat;
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
                        "identifier"
                })
        }
)
public class Function {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String nodeIdentifier;

    private String structureAddress;

    private String identifier;

    @Enumerated(EnumType.STRING)
    private DataFormat inputDataFormat;

    private String inputDataSchema;

    @Enumerated(EnumType.STRING)
    private DataFormat outputDataFormat;

    private String outputDataSchema;

    private String displayName;

    private String description;

    private String creator;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
