package com.crossborder.securecomm.kafka;

public class KafkaMessage {
    private String sender;
    private String receiver;
    private String encryptedPayload;
    private String signature;
    private String encryptedAesKey;

    public String getEncryptedAesKey() {
		return encryptedAesKey;
	}
	public void setEncryptedAesKey(String encryptedAesKey) {
		this.encryptedAesKey = encryptedAesKey;
	}
	public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }

    public String getEncryptedPayload() { return encryptedPayload; }
    public void setEncryptedPayload(String encryptedPayload) { this.encryptedPayload = encryptedPayload; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
}
