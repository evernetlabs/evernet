package org.evernet.util;

import lombok.experimental.UtilityClass;
import org.evernet.bean.Fingerprint;
import tools.jackson.databind.ObjectMapper;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

@UtilityClass
public class FingerprintHelper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public String encode(
            PrivateKey privateKey,
            PublicKey publicKey,
            Fingerprint.Actor actor
    ) throws Exception {
        String publicKeyB64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        Fingerprint.Payload payload = Fingerprint.Payload.builder()
                .actor(actor)
                .signingPublicKey(publicKeyB64)
                .build();

        byte[] payloadBytes = OBJECT_MAPPER.writeValueAsBytes(payload);

        byte[] signature = SignatureHelper.sign(payloadBytes, privateKey);

        Fingerprint fingerprint = Fingerprint.builder()
                .payload(payload)
                .signature(Base64.getEncoder().encodeToString(signature))
                .build();

        byte[] fingerprintBytes = OBJECT_MAPPER.writeValueAsBytes(fingerprint);
        return Base64.getEncoder().encodeToString(fingerprintBytes);
    }

    public boolean verify(String base64Fingerprint) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(base64Fingerprint);
        Fingerprint fingerprint = OBJECT_MAPPER.readValue(decoded, Fingerprint.class);

        Fingerprint.Payload payload = fingerprint.getPayload();
        byte[] payloadBytes = OBJECT_MAPPER.writeValueAsBytes(payload);
        byte[] signature = Base64.getDecoder().decode(fingerprint.getSignature());

        PublicKey publicKey = Ed25519KeyHelper.stringToPublicKey(payload.getSigningPublicKey());
        return SignatureHelper.verify(payloadBytes, signature, publicKey);
    }
}
