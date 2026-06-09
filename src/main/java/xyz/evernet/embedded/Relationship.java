package xyz.evernet.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.evernet.enums.RelationshipType;

import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
public class Relationship {

    @NotBlank(message = "Display name is required")
    private String displayName;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Target structure address is required")
    private String targetStructureAddress;

    @NotNull(message = "Relationship type is required")
    private RelationshipType type;

    private Set<String> allowedRoles;
}
