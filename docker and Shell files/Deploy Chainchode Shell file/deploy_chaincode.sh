#!/bin/bash

# Chaincode parameters
CC_NAME="basic"
CC_VERSION="2.0"
CC_SEQUENCE="52"
CC_LABEL="${CC_NAME}_${CC_VERSION}"
CC_PACKAGE_ID="basic_2.0:02bc6e5132d102d25999beddfbb638f4121e500c14072534bec133b467279ff8"

CHANNEL_NAME="mychannel"
ORDERER_ADDRESS="localhost:7050"
ORDERER_TLS_HOSTNAME="orderer.example.com"

echo "🔐 Approving chaincode for Org1..."
export CORE_PEER_LOCALMSPID="Org1MSP"
export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
export CORE_PEER_ADDRESS=localhost:7051
export CORE_PEER_TLS_ROOTCERT_FILE=$PEER0_ORG1_CA

peer lifecycle chaincode approveformyorg \
  -o $ORDERER_ADDRESS \
  --ordererTLSHostnameOverride $ORDERER_TLS_HOSTNAME \
  --channelID $CHANNEL_NAME \
  --name $CC_NAME \
  --version $CC_VERSION \
  --package-id $CC_PACKAGE_ID \
  --sequence $CC_SEQUENCE \
  --tls \
  --cafile $ORDERER_CA \
  --signature-policy "OR('Org1MSP.peer','Org2MSP.peer')"

echo "🔐 Approving chaincode for Org2..."
export CORE_PEER_LOCALMSPID="Org2MSP"
export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp
export CORE_PEER_ADDRESS=localhost:9051
export CORE_PEER_TLS_ROOTCERT_FILE=$PEER0_ORG2_CA

peer lifecycle chaincode approveformyorg \
  -o $ORDERER_ADDRESS \
  --ordererTLSHostnameOverride $ORDERER_TLS_HOSTNAME \
  --channelID $CHANNEL_NAME \
  --name $CC_NAME \
  --version $CC_VERSION \
  --package-id $CC_PACKAGE_ID \
  --sequence $CC_SEQUENCE \
  --tls \
  --cafile $ORDERER_CA \
  --signature-policy "OR('Org1MSP.peer','Org2MSP.peer')"

echo "✅ Committing chaincode..."
peer lifecycle chaincode commit \
  -o $ORDERER_ADDRESS \
  --ordererTLSHostnameOverride $ORDERER_TLS_HOSTNAME \
  --channelID $CHANNEL_NAME \
  --name $CC_NAME \
  --version $CC_VERSION \
  --sequence $CC_SEQUENCE \
  --tls \
  --cafile $ORDERER_CA \
  --peerAddresses localhost:7051 \
  --tlsRootCertFiles $PEER0_ORG1_CA \
  --peerAddresses localhost:9051 \
  --tlsRootCertFiles $PEER0_ORG2_CA \
  --signature-policy "OR('Org1MSP.peer','Org2MSP.peer')"

echo "✅ Chaincode deployment complete!"
