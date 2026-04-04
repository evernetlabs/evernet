package xyz.evernet.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.evernet.embedded.Event;
import xyz.evernet.embedded.Function;
import xyz.evernet.embedded.Property;
import xyz.evernet.embedded.Relationship;

import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
public class StructureCreationRequest {

    @NotBlank(message = "Identifier is required")
    private String identifier;

    @NotBlank(message = "Display name is required")
    private String displayName;

    private String description;

    private Map<String, @Valid Property> properties;

    private Map<String, @Valid Event> events;

    private Map<String, @Valid Relationship> relationships;

    private Map<String, @Valid Function> functions;
}
