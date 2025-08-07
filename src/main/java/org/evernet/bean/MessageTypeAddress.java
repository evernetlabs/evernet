package org.evernet.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
public class MessageTypeAddress {

    private String vertexEndpoint;

    private String nodeIdentifier;

    private String identifier;

    public String toString() {
        return String.format("%s/%s/%s", vertexEndpoint, nodeIdentifier, identifier);
    }

    public static MessageTypeAddress fromString(String address) {
        String[] components = address.split("/");

        if (components.length != 3) {
            throw new IllegalArgumentException(String.format("Invalid message type address %s", address));
        }

        return MessageTypeAddress.builder()
                .vertexEndpoint(components[0])
                .nodeIdentifier(components[1])
                .identifier(components[2])
                .build();
    }
}
