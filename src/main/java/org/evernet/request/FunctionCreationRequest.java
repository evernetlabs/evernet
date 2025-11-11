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
public class FunctionCreationRequest {

    @NotBlank(message = "Identifier is required")
    private String identifier;

    @NotNull(message = "Input data format is required")
    private DataFormat inputDataFormat;

    private String inputDataSchema;
    @NotNull(message = "Output data format is required")
    private DataFormat outputDataFormat;

    private String outputDataSchema;

    @NotBlank(message = "Display name is required")
    private String displayName;

    private String description;
}
