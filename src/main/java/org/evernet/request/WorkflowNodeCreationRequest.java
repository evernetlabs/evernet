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
public class WorkflowNodeCreationRequest {

    @NotBlank(message = "Identifier is required")
    private String identifier;

    @NotBlank(message = "Display name is required")
    private String displayName;

    private String description;

    private String functionParentReferenceChain;

    @NotBlank(message = "Function identifier is required")
    private String functionIdentifier;
}
