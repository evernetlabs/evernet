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
import xyz.evernet.embedded.Event;
import xyz.evernet.embedded.Function;
import xyz.evernet.embedded.Property;

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

    private String creator;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
