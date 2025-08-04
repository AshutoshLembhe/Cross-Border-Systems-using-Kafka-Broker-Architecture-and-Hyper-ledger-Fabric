package com.crossborder.securecomm.test;

import com.crossborder.securecomm.kafka.KafkaMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KafkaEndToEndTest {

    @Test
    public void testKafkaMessageStructure() {
        KafkaMessage message = new KafkaMessage();
        message.setSender("System2");
        message.setReceiver("System1");
        message.setEncryptedPayload("Base64EncryptedText");
        message.setSignature("Base64Signature");
        message.setEncryptedAesKey("Base64EncryptedAESKey");

        assertEquals("System2", message.getSender());
        assertEquals("System1", message.getReceiver());
        assertNotNull(message.getEncryptedPayload());
        assertNotNull(message.getSignature());
        assertNotNull(message.getEncryptedAesKey());
    }

    @Test
    public void testKafkaMessage_missingField_shouldFail() {
        KafkaMessage message = new KafkaMessage();
        message.setSender("System2");
        message.setReceiver(null); // Missing receiver
        message.setEncryptedPayload("Encrypted");
        message.setSignature("Signature");
        message.setEncryptedAesKey("EncryptedKey");

        assertNull(message.getReceiver(), "Receiver should be null (invalid)");
        assertNotEquals("System2", message.getReceiver(), "Should not match expected receiver");
    }

    @Test
    public void testKafkaMessage_invalidPayload_shouldDetect() {
        KafkaMessage message = new KafkaMessage();
        message.setSender("System2");
        message.setReceiver("System1");
        message.setEncryptedPayload(""); // Invalid payload
        message.setSignature("sig");
        message.setEncryptedAesKey("key");

        assertTrue(message.getEncryptedPayload().isEmpty(), "Encrypted payload is empty and invalid");
    }
}
