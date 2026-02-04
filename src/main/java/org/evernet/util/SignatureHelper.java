package org.evernet.util;

import lombok.experimental.UtilityClass;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

@UtilityClass
public class SignatureHelper {


    public byte[] sign(byte[] data, PrivateKey privateKey) throws Exception {
        Signature sig = Signature.getInstance("Ed25519");
        sig.initSign(privateKey);
        sig.update(data);
        return sig.sign();
    }

    public boolean verify(
            byte[] data,
            byte[] signature,
            PublicKey publicKey
    ) throws Exception {
        Signature sig = Signature.getInstance("Ed25519");
        sig.initVerify(publicKey);
        sig.update(data);
        return sig.verify(signature);
    }
}
