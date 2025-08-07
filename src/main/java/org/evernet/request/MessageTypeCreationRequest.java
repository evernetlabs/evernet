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
public class MessageTypeCreationRequest {

    @NotBlank(message = "Identifier is required")
    private String identifier;

    @NotBlank(message = "Display name is required")
    private String displayName;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "JSON schema is required")
    private String jsonSchema;
}
