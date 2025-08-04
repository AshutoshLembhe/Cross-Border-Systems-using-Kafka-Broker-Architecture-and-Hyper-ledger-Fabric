package com.crossborder.securecomm.kafka;

import com.crossborder.securecomm.crypto.AesUtil;
import com.crossborder.securecomm.crypto.EcdsaUtil;
import com.crossborder.securecomm.crypto.KeyUtil;
import com.crossborder.securecomm.fabric.FabricService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;

@Component
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final FabricService fabricService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PrivateKey privateKey;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate, FabricService fabricService) {
        this.kafkaTemplate = kafkaTemplate;
        this.fabricService = fabricService;
        this.privateKey = KeyUtil.loadPrivateKey("src/main/resources/key.pem"); // Sender's private key
    }

    public void sendEncryptedMessage(String topic, String sender, String receiver, String message) {
        try {
            // 1. Generate AES key dynamically
            SecretKey aesKey = AesUtil.generateRandomAesKey();

            // 2. Encrypt payload
            String encryptedPayload = AesUtil.encrypt(message, aesKey);

            // 3. Sign payload
            String signature = EcdsaUtil.sign(encryptedPayload, privateKey);

            // 4. Fetch receiver’s public key from Fabric
            String receiverCertPem = fabricService.getUserPublicKey(receiver);
            PublicKey receiverPublicKey = KeyUtil.extractPublicKeyFromCertificatePem(receiverCertPem);

            // 5. Encrypt AES key with receiver’s public key
            String encryptedAesKey = KeyUtil.encryptAesKey(aesKey, receiverPublicKey);

            // 6. Create and send Kafka message
            KafkaMessage msg = new KafkaMessage();
            msg.setSender(sender);
            msg.setReceiver(receiver);
            msg.setEncryptedPayload(encryptedPayload);
            msg.setSignature(signature);
            msg.setEncryptedAesKey(encryptedAesKey);

            kafkaTemplate.send(topic, objectMapper.writeValueAsString(msg));
            System.out.println("Encrypted message sent to Kafka - topic: " + topic);

        } catch (Exception e) {
            System.err.println("KafkaProducer Error: Failed to send encrypted message");
            e.printStackTrace();
        }
    }
}
