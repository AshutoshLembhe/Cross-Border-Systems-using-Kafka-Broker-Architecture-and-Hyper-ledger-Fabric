package com.crossborder.securecomm.fabric;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.hyperledger.fabric.gateway.*;

import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.springframework.stereotype.Service;
import org.hyperledger.fabric.sdk.User;

import java.util.Set;

@Service
public class FabricService {

    private final Gateway gateway;
    private final Network network;
    private final Contract contract;

    public FabricService() throws Exception {
        // Load the certificate and key
        Path certPath = Paths.get("src", "main", "resources", "cert.pem");
        Path keyPath = Paths.get("src", "main", "resources", "key.pem");

        X509Certificate certificate = Identities.readX509Certificate(Files.newBufferedReader(certPath));
        PrivateKey privateKey = Identities.readPrivateKey(Files.newBufferedReader(keyPath));
        Identity identity = Identities.newX509Identity("Org1MSP", certificate, privateKey);

        // Prepare an in-memory wallet
        Wallet wallet = Wallets.newInMemoryWallet();
        wallet.put("appUser", identity);

        // Load the connection profile
        Path networkConfigPath = Paths.get("src", "main", "resources", "connection-org1.json");

        // Build gateway
        Gateway.Builder builder = Gateway.createBuilder()
            .identity(wallet, "appUser")
            .networkConfig(networkConfigPath)
            .discovery(false); // Discovery false for static config

        this.gateway = builder.connect();
        this.network = gateway.getNetwork("mychannel");
        this.contract = network.getContract("basic");
    }

    public String getAllAssets() throws Exception {
        byte[] result = contract.evaluateTransaction("GetAllAssets");
        return new String(result);
    }

    public String createAsset(String id, String color, String size, String owner, String value) throws Exception {
        byte[] result = contract.submitTransaction("CreateAsset", id, color, size, owner, value);
        return new String(result);
    }
    
    public void registerUserPublicKey(String userId, String certPem) throws Exception {
        contract.submitTransaction("RegisterUserPublicKey", userId, certPem);
    }
    
    public String getUserPublicKey(String userId) throws Exception {
        byte[] result = contract.evaluateTransaction("GetUserPublicKey", userId);
        return new String(result);
    }

    public void disconnect() {
        if (gateway != null) {
            gateway.close();
        }
    }
}
