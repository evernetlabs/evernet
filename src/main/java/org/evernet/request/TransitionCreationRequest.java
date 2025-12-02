package org.evernet.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
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
public class TransitionCreationRequest {

    @NotBlank(message = "Source state identifier is required")
    private String sourceStateIdentifier;

    @NotBlank(message = "Target state identifier is required")
    private String targetStateIdentifier;

    private String eventParentReferenceChain;

    @NotBlank(message = "Event identifier is required")
    private String eventIdentifier;

    @NotBlank(message = "Identifier is required")
    private String identifier;

    @NotBlank(message = "Display name is required")
    private String displayName;

    private String description;
}
