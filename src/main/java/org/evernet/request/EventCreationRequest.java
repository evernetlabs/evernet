package org.evernet.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.evernet.enums.DataFormat;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
public class EventCreationRequest {

    @NotBlank(message = "Identifier is required")
    private String identifier;

    @NotNull(message = "Data format is required")
    private DataFormat dataFormat;

    private String dataSchema;

    @NotBlank(message = "Display name is required")
    private String displayName;

    private String description;
}
