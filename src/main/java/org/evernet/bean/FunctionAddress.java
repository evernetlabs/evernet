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
public class FunctionAddress {

    private String vertexEndpoint;

    private String nodeIdentifier;

    private String identifier;


    public String toString() {
        return String.format("%s/%s/functions/%s", vertexEndpoint, nodeIdentifier, identifier);
    }

    public static FunctionAddress fromString(String address) {
        String[] components = address.split("/");

        if (components.length != 4) {
            throw new IllegalArgumentException(String.format("Invalid function address %s", address));
        }

        if (!components[2].equals("functions")) {
            throw new IllegalArgumentException(String.format("Invalid function address %s", address));
        }

        return FunctionAddress.builder()
                .vertexEndpoint(components[0])
                .nodeIdentifier(components[1])
                .identifier(components[3])
                .build();
    }
}
