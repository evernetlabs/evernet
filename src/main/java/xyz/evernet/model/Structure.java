package xyz.evernet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;
import xyz.evernet.embedded.Event;
import xyz.evernet.embedded.Function;
import xyz.evernet.embedded.Property;
import xyz.evernet.exception.ClientException;
import xyz.evernet.util.Json;
import xyz.evernet.util.JsonSchemaUtil;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
@Document
public class Structure {

    @Id
    private String id;

    @Indexed(unique = true)
    private String identifier;

    private String displayName;

    private String description;

    private Map<String, Property> properties;

    private Map<String, Function> functions;

    private Map<String, Event> events;

    private Set<String> managementRoles;

    private String creator;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public Map<String, Object> validateProperties(Map<String, Object> properties) {
        Map<String, Object> validatedProperties = new HashMap<>();

        if (properties == null) {
            return validatedProperties;
        }

        if (this.properties == null) {
            throw new ClientException("No properties can be set against structure %s".formatted(this.getIdentifier()));
        }

        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String propertyIdentifier = entry.getKey();
            Object propertyValue = entry.getValue();

            Property propertyDefinition = this.properties.get(propertyIdentifier);
            if (propertyDefinition == null) {
                throw new ClientException("Property %s is not defined in the structure".formatted(propertyIdentifier));
            }

            if (StringUtils.hasText(propertyDefinition.getSchema())) {
                if (!JsonSchemaUtil.isValidJson(propertyDefinition.getSchema(), Json.encode(propertyValue))) {
                    throw new ClientException("Property %s does not conform to the defined schema".formatted(propertyIdentifier));
                }
            }

            validatedProperties.put(propertyIdentifier, propertyValue);
        }

        return validatedProperties;
    }
}
