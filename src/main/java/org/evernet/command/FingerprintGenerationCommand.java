package org.evernet.command;

import org.evernet.bean.Fingerprint;
import org.evernet.util.Ed25519KeyHelper;
import org.evernet.util.FingerprintHelper;

import java.util.Scanner;

public class FingerprintGenerationCommand {

    static void main() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter ED25519 public key: ");
        String publicKey = scanner.nextLine();

        System.out.println("Enter ED25519 private key: ");
        String privateKey = scanner.nextLine();

        System.out.println("Enter Actor Display Name: ");
        String displayName = scanner.nextLine();

        System.out.println("Enter Actor Description: ");
        String description = scanner.nextLine();

        System.out.println("Enter Actor Type: ");
        String actorType = scanner.nextLine();

        String fingerprintHelper = FingerprintHelper.encode(
                Ed25519KeyHelper.stringToPrivateKey(privateKey),
                Ed25519KeyHelper.stringToPublicKey(publicKey),
                new Fingerprint.Actor(displayName, description, actorType)
        );

        System.out.println("Generated Fingerprint:");
        System.out.println(fingerprintHelper);
    }
}
