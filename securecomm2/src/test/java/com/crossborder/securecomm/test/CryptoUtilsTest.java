package com.crossborder.securecomm.test;

import com.crossborder.securecomm.crypto.AesUtil;
import com.crossborder.securecomm.crypto.EcdsaUtil;
import com.crossborder.securecomm.crypto.KeyUtil;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class CryptoUtilsTest {

    @Test
    public void testAesEncryptDecrypt_validData() throws Exception {
        String plainText = "SensitiveMessage";
        SecretKey key = AesUtil.generateRandomAesKey();

        String encrypted = AesUtil.encrypt(plainText, key);
        String decrypted = AesUtil.decrypt(encrypted, key);

        assertEquals(plainText, decrypted);
    }

    @Test
    public void testAesEncryptDecrypt_wrongKey_shouldFail() throws Exception {
        String message = "SecureText";
        SecretKey key1 = AesUtil.generateRandomAesKey();
        SecretKey key2 = AesUtil.generateRandomAesKey();

        String encrypted = AesUtil.encrypt(message, key1);
        String decrypted;

        try {
            decrypted = AesUtil.decrypt(encrypted, key2);
            
            assertNotEquals(message, decrypted, "Decryption should not yield original message with wrong key");
        } catch (Exception e) {
            // Acceptable: decryption fails with exception (padding/cipher mismatch)
            assertTrue(true, "Decryption failed as expected with wrong key");
        }
    }


    @Test
    public void testEcdsaSignVerify_withValidPemKeys() throws Exception {
        String data = "Verifiable Payload";

        PrivateKey privateKey = KeyUtil.loadPrivateKey("src/main/resources/key.pem");
        String certPem = new String(Files.readAllBytes(Paths.get("src/main/resources/cert.pem")));
        PublicKey publicKey = KeyUtil.extractPublicKeyFromCertificatePem(certPem);

        String signature = EcdsaUtil.sign(data, privateKey);
        boolean isValid = EcdsaUtil.verify(data, signature, publicKey);

        assertTrue(isValid, "Signature must be valid with correct public key");
    }

    @Test
    public void testEcdsaSignVerify_modifiedMessage_shouldFail() throws Exception {
        String original = "This is my secure message";
        String tampered = "This is my modified message";

        PrivateKey privateKey = KeyUtil.loadPrivateKey("src/main/resources/key.pem");
        String certPem = new String(Files.readAllBytes(Paths.get("src/main/resources/cert.pem")));
        PublicKey publicKey = KeyUtil.extractPublicKeyFromCertificatePem(certPem);

        String signature = EcdsaUtil.sign(original, privateKey);
        boolean isValid = EcdsaUtil.verify(tampered, signature, publicKey);

        assertFalse(isValid, "Signature verification must fail if message is tampered");
    }

    @Test
    public void testKeyUtil_loadInvalidPath_shouldThrowException() {
        assertThrows(Exception.class, () -> {
            KeyUtil.loadPrivateKey("invalid/path/to/key.pem");
        });
    }

    @Test
    public void testKeyUtil_extractInvalidCert_shouldThrowException() {
        assertThrows(Exception.class, () -> {
            String invalidPem = "-----BEGIN CERTIFICATE-----\\nInvalid\\n-----END CERTIFICATE-----";
            KeyUtil.extractPublicKeyFromCertificatePem(invalidPem);
        });
    }
}
