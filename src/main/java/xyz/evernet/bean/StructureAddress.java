package xyz.evernet.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.evernet.exception.ClientException;

import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
public class StructureAddress {

    private String  vertexEndpoint;

    private String identifier;

    public String toString() {
        return "%s/s/%s".formatted(vertexEndpoint, identifier);
    }

    public static StructureAddress from(String str) {
        String[] components = str.split("/");

        if (components.length != 3) {
            throw new ClientException("Invalid structure address %s".formatted(str));
        }

        if (!Objects.equals(components[1], "s")) {
            throw new ClientException("Invalid structure address %s".formatted(str));
        }

        return StructureAddress.builder()
                .vertexEndpoint(components[0])
                .identifier(components[2])
                .build();
    }
}
