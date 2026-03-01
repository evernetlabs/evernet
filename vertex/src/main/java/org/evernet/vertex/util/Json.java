package org.evernet.vertex.util;

import lombok.experimental.UtilityClass;
import tools.jackson.databind.ObjectMapper;

@UtilityClass
public class Json {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Object decode(String s, Class<?> c) {
        Object u = null;

        if (s != null) {
            try {
                u = objectMapper.readerFor(c).readValue(s);
            } catch (Exception e) {
                // Do nothing
            }
        }

        return u;
    }

    public String encode(Object o) {
        if (o == null) {
            return "";
        }

        return objectMapper.writeValueAsString(o);
    }
}