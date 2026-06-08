package xyz.evernet.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.evernet.exception.ClientException;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
public class UserAddress {

    private String vertexEndpoint;

    private String username;

    public String toString() {
        return "%s/%s".formatted(vertexEndpoint, username);
    }

    public static UserAddress from(String str) {
        String[] components = str.split("/");

        if (components.length != 2) {
            throw new ClientException("Invalid user address %s".formatted(str));
        }

        return UserAddress.builder()
                .vertexEndpoint(components[0])
                .username(components[1])
                .build();
    }
}
