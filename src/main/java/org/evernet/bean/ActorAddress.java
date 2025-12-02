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
public class ActorAddress {

    private String identifier;

    private NodeAddress nodeAddress;

    public static ActorAddress fromString(String s) {
        String[] components = s.split("/");
        if (components.length != 3) {
            throw new IllegalArgumentException(String.format("Invalid actor address %s", s));
        }

        return ActorAddress.builder()
                .identifier(components[2])
                .nodeAddress(NodeAddress.builder().vertexEndpoint(components[0]).identifier(components[1]).build())
                .build();
    }

    public String toString() {
        return String.format("%s/%s", nodeAddress.toString(), identifier);
    }
}