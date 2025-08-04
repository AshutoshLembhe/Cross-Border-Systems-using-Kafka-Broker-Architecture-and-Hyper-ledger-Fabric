package com.crossborder.securecomm.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class KeyUtil {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static SecretKey generateAesKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        return keyGen.generateKey();
    }

    public static String encryptAesKey(SecretKey aesKey, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("ECIES", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedKey = cipher.doFinal(aesKey.getEncoded());
        return Base64.getEncoder().encodeToString(encryptedKey);
    }

    public static SecretKey decryptAesKey(String base64EncryptedKey, PrivateKey privateKey) throws Exception {
        byte[] encryptedKeyBytes = Base64.getDecoder().decode(base64EncryptedKey);
        Cipher cipher = Cipher.getInstance("ECIES", "BC");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedKeyBytes = cipher.doFinal(encryptedKeyBytes);
        return new SecretKeySpec(decryptedKeyBytes, "AES");
    }

    public static PublicKey extractPublicKeyFromCertificatePem(String pem) {
        try {
            pem = pem.replace("-----BEGIN CERTIFICATE-----", "")
                     .replace("-----END CERTIFICATE-----", "")
                     .replaceAll("\\s+", "");
            byte[] certBytes = Base64.getDecoder().decode(pem);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) cf.generateCertificate(new java.io.ByteArrayInputStream(certBytes));
            return certificate.getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse public key from certificate PEM", e);
        }
    }

    public static PrivateKey loadPrivateKey(String path) {
        try {
            String pem = new String(Files.readAllBytes(Paths.get(path)));
            pem = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                     .replace("-----END PRIVATE KEY-----", "")
                     .replaceAll("\\s+", "");
            byte[] keyBytes = Base64.getDecoder().decode(pem);

            KeyFactory kf = KeyFactory.getInstance("EC", "BC");
            return kf.generatePrivate(new java.security.spec.PKCS8EncodedKeySpec(keyBytes));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load private key from file", e);
        }
    }
}
