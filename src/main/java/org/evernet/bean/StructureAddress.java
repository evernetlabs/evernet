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
public class StructureAddress {

    private String identifier;

    private NodeAddress nodeAddress;

    public String toString() {
        return String.format("%s/structures/%s", nodeAddress.toString(), identifier);
    }

    public static StructureAddress fromString(String s) {
        String[] components = s.split("/structures/");

        if (components.length != 2) {
            throw new IllegalArgumentException(String.format("Invalid structure address %s", s));
        }

        return StructureAddress.builder()
                .nodeAddress(NodeAddress.fromString(components[0]))
                .identifier(components[1])
                .build();
    }
}
