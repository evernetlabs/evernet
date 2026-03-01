package org.evernet.vertex.util;

import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;

@UtilityClass
public class Random {

    public String generateRandomString(int length) {
        byte[] array = new byte[4096];
        new java.util.Random().nextBytes(array);

        String randomString = new String(array, StandardCharsets.UTF_8);

        StringBuilder stringBuilder = new StringBuilder();

        for (int k = 0; k < randomString.length(); k++) {

            char ch = randomString.charAt(k);

            if (((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9')) && (length > 0)) {
                stringBuilder.append(ch);
                length--;
            }
        }

        return stringBuilder.toString();
    }
}