package com.crossborder.securecomm.test;

import com.crossborder.securecomm.fabric.FabricService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FabricIntegrationTest {

    private FabricService fabricService;

    @BeforeEach
    public void setup() throws Exception {
        // This handles any exception thrown by FabricService constructor
        fabricService = new FabricService();
    }

    @Test
    public void testFabric_getUserPublicKey_validUser() throws Exception {
        String userId = "System2";
        String cert = fabricService.getUserPublicKey(userId);

        assertNotNull(cert, "Should return valid PEM certificate");
        assertTrue(cert.contains("BEGIN CERTIFICATE"), "Returned string must be a PEM certificate");
    }

    @Test
    public void testFabric_getUserPublicKey_missingUser_shouldFail() {
        String userId = "unknownUser999";
        Exception exception = assertThrows(Exception.class, () -> {
            fabricService.getUserPublicKey(userId);
        });

        String message = exception.getMessage();
        assertTrue(message.contains("no public key registered"), "Should throw missing key error");
    }


    @Test
    public void testFabric_registerPublicKey_noCrash() {
        String certPem = "-----BEGIN CERTIFICATE-----\n...stub...\n-----END CERTIFICATE-----";
        assertDoesNotThrow(() -> fabricService.registerUserPublicKey("TestUser", certPem));
    }
}
