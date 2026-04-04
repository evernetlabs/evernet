package xyz.evernet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import xyz.evernet.embedded.Event;
import xyz.evernet.embedded.Function;
import xyz.evernet.embedded.Property;
import xyz.evernet.embedded.Relationship;

import java.time.Instant;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
@Document
@CompoundIndexes({
        @CompoundIndex(
                def = "{'nodeIdentifier': 1, 'address': 1}",
                unique = true
        )
})
public class Structure {

    @Id
    private String id;

    private String nodeIdentifier;

    private String address;

    private String displayName;

    private String description;

    private Map<String, Property> properties;

    private Map<String, Event> events;

    private Map<String, Relationship> relationships;

    private Map<String, Function> functions;

    private String creator;

    private Instant createdAt;

    private Instant updatedAt;
}
