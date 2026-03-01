package org.evernet.vertex.util;

import lombok.experimental.UtilityClass;
import org.mindrot.jbcrypt.BCrypt;

@UtilityClass
public class Password {

    public String hash(String password) {
        String salt = BCrypt.gensalt(12);
        return BCrypt.hashpw(password, salt);
    }

    public boolean verify(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}