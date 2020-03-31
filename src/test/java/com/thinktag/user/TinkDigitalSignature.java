package com.thinktag.user;

import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.KeysetReader;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.config.TinkConfig;
import com.google.crypto.tink.proto.EcdsaPrivateKey;
import com.google.crypto.tink.proto.EcdsaPublicKey;
import com.google.crypto.tink.proto.EllipticCurveType;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.Keyset;
import com.google.crypto.tink.signature.SignatureKeyTemplates;
import com.google.crypto.tink.signature.SignaturePemKeysetReader;
import com.google.crypto.tink.subtle.EcdsaSignJce;
import com.google.crypto.tink.subtle.EcdsaVerifyJce;
import com.google.crypto.tink.subtle.EllipticCurves;
import com.google.crypto.tink.subtle.Enums;
import com.google.crypto.tink.subtle.PemKeyType;
import com.google.protobuf.ExtensionRegistryLite;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

/**
 * /test/java/com/google/crypto/tink/subtle/PemKeyTypeTest.java
 */
public class TinkDigitalSignature {

    @BeforeClass
    public static void beforeClass() throws Exception {
        TinkConfig.register();
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void testSignature() throws Exception {
        byte[] data = "Hello world".getBytes(StandardCharsets.UTF_8);
        // 1. Generate the private key material.
        KeysetHandle privateKeysetHandle = KeysetHandle.generateNew(
                SignatureKeyTemplates.ECDSA_P256);
        // 2. Get the primitive.
        PublicKeySign signer = privateKeysetHandle.getPrimitive(PublicKeySign.class);
        // 3. Use the primitive to sign.
        byte[] signature = signer.sign(data);
        // VERIFYING
        // 1. Obtain a handle for the public key material.
        KeysetHandle publicKeysetHandle =
                privateKeysetHandle.getPublicKeysetHandle();
        // 2. Get the primitive.
        PublicKeyVerify verifier = publicKeysetHandle.getPrimitive(PublicKeyVerify.class);
        // 4. Use the primitive to verify.
        verifier.verify(signature, data);

    }

    @Test
    public void testKeyUsage() throws Exception {
        String privatePem = "-----BEGIN EC PRIVATE KEY-----\n" +
                "MHcCAQEEILRAtMmuVXz+D0a1gpJ0pS6svveJ4eMRgNTtctlZii2aoAoGCCqGSM49\n" +
                "AwEHoUQDQgAEo08CzYAJSq5ok1x4f4ZS4NZNBKgp54nm9298raU0MvugvocnQtQy\n" +
                "LhHJi1Mk6qcnFdNpIELLQMQNZelSk21mfw==\n" +
                "-----END EC PRIVATE KEY-----\n";

        String publicPem = "-----BEGIN PUBLIC KEY-----\n" +
                "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEo08CzYAJSq5ok1x4f4ZS4NZNBKgp\n" +
                "54nm9298raU0MvugvocnQtQyLhHJi1Mk6qcnFdNpIELLQMQNZelSk21mfw==\n" +
                "-----END PUBLIC KEY-----\n";

        PrivateKey privateKey = getPrivateKey(privatePem);
        PublicKey publicKey = getPublicKey(publicPem);
        byte[] signature = getPrimitive(privateKey).sign("hello".getBytes());
        getPrimitive(publicKey).verify(signature, "hello".getBytes());
    }
    private PrivateKey getPrivateKey(String privatePem) throws Exception {
        PEMParser pemParser = new PEMParser(new StringReader(privatePem));
        return new JcaPEMKeyConverter().getPrivateKey(((PEMKeyPair) pemParser.readObject()).getPrivateKeyInfo());
    }
    private PublicKey getPublicKey(String publicPem) throws Exception {
        PEMParser pemParser = new PEMParser(new StringReader(publicPem));
        return new JcaPEMKeyConverter().getPublicKey((SubjectPublicKeyInfo) pemParser.readObject());
    }
    private PublicKeySign getPrimitive(PrivateKey key) throws GeneralSecurityException {
        return new EcdsaSignJce(
                (ECPrivateKey) key,
                Enums.HashType.SHA256,
                EllipticCurves.EcdsaEncoding.DER);
    }
    private PublicKeyVerify getPrimitive(PublicKey publicKey)
            throws GeneralSecurityException {
        return new EcdsaVerifyJce(
                (ECPublicKey) publicKey,
                Enums.HashType.SHA256,
                EllipticCurves.EcdsaEncoding.DER);
    }
}
