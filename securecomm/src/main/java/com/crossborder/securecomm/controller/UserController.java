package com.crossborder.securecomm.controller;

import com.crossborder.securecomm.fabric.FabricService;
import com.crossborder.securecomm.kafka.KafkaProducer;
import com.crossborder.securecomm.model.User;
import com.crossborder.securecomm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private FabricService fabricService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            String receiver = user.getBlockChainAddress();

            //  Validate input
            if (receiver == null || receiver.isBlank()) {
                return ResponseEntity.badRequest().body("Missing blockChainAddress");
            }

            // Fetch public key PEM from Fabric
            String pem = fabricService.getUserPublicKey(receiver);
            if (pem == null || pem.isBlank()) {
                return ResponseEntity.status(500).body("Public key not found for: " + receiver);
            }

            // Send a sample encrypted message to the receiver via Kafka
            kafkaProducer.sendEncryptedMessage("secure-topic", "System1", receiver, "Welcome");

            // Save user metadata (we won’t store publicKey anymore)
            user.setPublicKey("");  // Optional: you can drop this field
            user.setTimestamp(Instant.now().getEpochSecond());
            userRepository.save(user);

            return ResponseEntity.ok("User registered and secure message sent to: " + receiver);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
