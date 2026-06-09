package xyz.evernet.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.evernet.enums.NodeEventType;

import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
public class NodeFederationRequest {

    @NotNull(message = "Event data is required")
    private Object event;

    @NotNull(message = "Event type is required")
    private NodeEventType eventType;

    @NotBlank(message = "Requester address is required")
    private String requesterAddress;

    @NotEmpty(message = "Target user addresses are required")
    private Set<String> targetUserAddresses;
}
