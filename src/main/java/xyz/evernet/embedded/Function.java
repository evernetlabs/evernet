package xyz.evernet.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.evernet.enums.FunctionType;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
public class Function {

    @NotBlank(message = "Function display name is required")
    private String displayName;

    private String description;

    @NotNull(message = "Function type is required")
    private FunctionType type;

    private Trigger trigger;

    private Rule precondition;

    private String inputJsonSchema;

    private String outputJsonSchema;

    @NotNull(message = "Function implementation is required")
    private FunctionImplementation implementation;
}
