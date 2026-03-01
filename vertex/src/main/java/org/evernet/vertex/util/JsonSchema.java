package org.evernet.vertex.util;

import com.github.erosb.jsonsKema.*;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonSchema {

    public Boolean isValidDefinition(String jsonSchema) {
        try {
            JsonValue schemaJson = new JsonParser(jsonSchema).parse();
            new SchemaLoader(schemaJson).load();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean matchWithSchema(String jsonSchema, String json) {
        JsonValue schemaJson = new JsonParser(jsonSchema).parse();
        Schema schema = new SchemaLoader(schemaJson).load();
        Validator validator = Validator.create(schema, new ValidatorConfig(FormatValidationPolicy.ALWAYS));
        JsonValue instance = new JsonParser(json).parse();
        ValidationFailure failure = validator.validate(instance);
        return failure == null;
    }
}