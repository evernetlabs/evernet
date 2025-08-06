package org.evernet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
@Entity
@Table(name = "actor_messaging_configs")
public class ActorMessagingConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String nodeIdentifier;

    private String actorIdentifier;

    private ConfigType configType;

    private String nodeAddress;

    private Integer priority;

    private String creator;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public enum ConfigType {
        TRANSMITTER_ADDRESS,
        RECEIVER_ADDRESS
    }
}
