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
@Table(name = "message_type_handlers")
@Entity
public class MessageTypeHandler {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String messageTypeAddress;

    private Location location;

    private String nodeIdentifier;

    private String actorAddress;

    private HandlerType handlerType;

    private String handlerAddress;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public enum HandlerType {
        PUSH,
        STORAGE_CALL,
        FUNCTION_CALL,
        WORKFLOW_CALL,
    }

    public enum Location {
        TRANSMITTER,
        RECEIVER
    }
}
