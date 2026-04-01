package xyz.evernet.embedded;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.evernet.util.Ed25519KeyHelper;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
public class SigningKey {

    @JsonIgnore
    private String privateKey;

    private String publicKey;

    public static SigningKey generate() throws NoSuchAlgorithmException {
        KeyPair keyPair = Ed25519KeyHelper.generateKeyPair();
        return SigningKey.builder()
                .privateKey(Ed25519KeyHelper.privateKeyToString(keyPair.getPrivate()))
                .publicKey(Ed25519KeyHelper.publicKeyToString(keyPair.getPublic()))
                .build();
    }

    @JsonIgnore
    public PrivateKey getPrivateKeyObject() throws Exception {
        return Ed25519KeyHelper.stringToPrivateKey(privateKey);
    }

    @JsonIgnore
    public PublicKey getPublicKeyObject() throws Exception {
        return Ed25519KeyHelper.stringToPublicKey(publicKey);
    }
}
