package org.evernet.command;

import org.evernet.util.Ed25519KeyHelper;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public class Ed25519GenerationCommand {

    static void main() throws NoSuchAlgorithmException {
        KeyPair keyPair = Ed25519KeyHelper.generateKeyPair();

        String privateKeyString = Ed25519KeyHelper.privateKeyToString(keyPair.getPrivate());
        String publicKeyString = Ed25519KeyHelper.publicKeyToString(keyPair.getPublic());

        System.out.println("Generated ED25519 Key Pair:");
        System.out.println("Private Key: " + privateKeyString);
        System.out.println("Public Key: " + publicKeyString);
    }
}
