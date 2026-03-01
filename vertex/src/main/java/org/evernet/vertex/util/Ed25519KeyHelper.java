package org.evernet.vertex.util;

import lombok.experimental.UtilityClass;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@UtilityClass
public class Ed25519KeyHelper {

    private static final String ALGORITHM = "Ed25519";

    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        return keyGen.generateKeyPair();
    }

    public String privateKeyToString(PrivateKey privateKey) {
        if (privateKey == null) {
            throw new IllegalArgumentException("Private key cannot be null");
        }
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public String publicKeyToString(PublicKey publicKey) {
        if (publicKey == null) {
            throw new IllegalArgumentException("Public key cannot be null");
        }
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public PrivateKey stringToPrivateKey(String keyString) throws Exception {
        if (keyString == null || keyString.trim().isEmpty()) {
            throw new IllegalArgumentException("Key string cannot be null or empty");
        }

        byte[] keyBytes = Base64.getDecoder().decode(keyString);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

    public PublicKey stringToPublicKey(String keyString) throws Exception {
        if (keyString == null || keyString.trim().isEmpty()) {
            throw new IllegalArgumentException("Key string cannot be null or empty");
        }

        byte[] keyBytes = Base64.getDecoder().decode(keyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }
}