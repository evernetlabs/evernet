package xyz.evernet.bean;

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
public class NodeAddress {

    private String identifier;

    private String vertexEndpoint;

    public static NodeAddress fromString(String s) {
        String[] components = s.split("/");

        if (components.length != 2) {
            throw new IllegalArgumentException(String.format("Invalid node address %s", s));
        }

        return NodeAddress.builder().vertexEndpoint(components[0]).identifier(components[1]).build();
    }

    public String toString() {
        return String.format("%s/%s", vertexEndpoint, identifier);
    }

    public Boolean equals(NodeAddress anotherNodeAddress) {
        return this.identifier.equals(anotherNodeAddress.getIdentifier()) && this.vertexEndpoint.equals(anotherNodeAddress.getVertexEndpoint());
    }
}
