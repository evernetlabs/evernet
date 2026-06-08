package xyz.evernet.util;

import com.github.erosb.jsonsKema.JsonParser;
import com.github.erosb.jsonsKema.JsonValue;
import com.github.erosb.jsonsKema.Schema;
import com.github.erosb.jsonsKema.SchemaLoader;
import com.github.erosb.jsonsKema.ValidationFailure;
import com.github.erosb.jsonsKema.Validator;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class JsonSchemaUtil {

    public boolean isValidSchema(String schemaJson) {
        try {
            JsonValue schemaValue = new JsonParser(schemaJson).parse();
            new SchemaLoader(schemaValue).load();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isValidJson(String schemaJson, String json) {
        try {
            JsonValue schemaValue = new JsonParser(schemaJson).parse();
            Schema schema = new SchemaLoader(schemaValue).load();

            JsonValue instance = new JsonParser(json).parse();

            ValidationFailure failure =
                    Validator.forSchema(schema).validate(instance);

            return failure == null;
        } catch (Exception e) {
            return false;
        }
    }
}
