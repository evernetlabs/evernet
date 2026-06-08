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
public class NodeAddress {


    private String  vertexEndpoint;

    private String id;

    public String toString() {
        return "%s/n/%s".formatted(vertexEndpoint, id);
    }

    public static NodeAddress from(String str) {
        String[] components = str.split("/");

        if (components.length != 3) {
            throw new ClientException("Invalid node address %s".formatted(str));
        }

        if (!Objects.equals(components[1], "n")) {
            throw new ClientException("Invalid node address %s".formatted(str));
        }

        return NodeAddress.builder()
                .vertexEndpoint(components[0])
                .id(components[2])
                .build();
    }
}
