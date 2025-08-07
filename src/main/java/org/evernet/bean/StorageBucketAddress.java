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
public class StorageBucketAddress {

    private String vertexEndpoint;

    private String nodeIdentifier;

    private String identifier;


    public String toString() {
        return String.format("%s/%s/storage/%s", vertexEndpoint, nodeIdentifier, identifier);
    }

    public static StorageBucketAddress fromString(String address) {
        String[] components = address.split("/");

        if (components.length != 4) {
            throw new IllegalArgumentException(String.format("Invalid storage bucket address %s", address));
        }

        if (!components[2].equals("storage")) {
            throw new IllegalArgumentException(String.format("Invalid storage bucket address %s", address));
        }

        return StorageBucketAddress.builder()
                .vertexEndpoint(components[0])
                .nodeIdentifier(components[1])
                .identifier(components[3])
                .build();
    }
}
