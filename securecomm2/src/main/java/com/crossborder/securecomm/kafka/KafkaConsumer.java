package com.crossborder.securecomm.kafka;

import com.crossborder.securecomm.crypto.AesUtil;
import com.crossborder.securecomm.crypto.EcdsaUtil;
import com.crossborder.securecomm.crypto.KeyUtil;
import com.crossborder.securecomm.fabric.FabricService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;

@Component
public class KafkaConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FabricService fabricService;
    private final PrivateKey privateKey;
    private final String receiverId;
    
    public KafkaConsumer(FabricService fabricService, @Value("${app.receiverId}") String receiverId) {
            this.fabricService = fabricService;
            this.receiverId = receiverId;
            this.privateKey = KeyUtil.loadPrivateKey("src/main/resources/key.pem");
        }


    @KafkaListener(topics = "secure-topic", groupId = "system2-group")
    public void consume(String json) {
        try {
            KafkaMessage msg = objectMapper.readValue(json, KafkaMessage.class);
            System.out.println("msg reciever ID: " + msg.getReceiver());
            if (!receiverId.equals(msg.getReceiver())) {
                System.out.println("Skipped message for " + msg.getReceiver() + ". This system is: " + receiverId);
                return;
            }
            
            // 1. Get sender’s public key from Fabric
            String senderCertPem = fabricService.getUserPublicKey(msg.getSender());
            PublicKey senderPublicKey = KeyUtil.extractPublicKeyFromCertificatePem(senderCertPem);

            // 2. Verify signature
            boolean verified = EcdsaUtil.verify(msg.getEncryptedPayload(), msg.getSignature(), senderPublicKey);
            if (!verified) {
                System.err.println("Signature verification failed. Message discarded.");
                return;
            }

            // 3. Decrypt AES key using receiver's private key
            SecretKey aesKey = KeyUtil.decryptAesKey(msg.getEncryptedAesKey(), privateKey);

            // 4. Decrypt payload
            String decryptedMessage = AesUtil.decrypt(msg.getEncryptedPayload(), aesKey);
            System.out.println("Message from " + msg.getSender() + ": " + decryptedMessage);

        } catch (Exception e) {
            System.err.println("KafkaConsumer Error: Failed to process message");
            e.printStackTrace();
        }
    }
}
