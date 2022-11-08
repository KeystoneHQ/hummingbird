package com.sparrowwallet.hummingbird.registry;

import com.sparrowwallet.hummingbird.registry.aptos.AptosSignRequest;
import com.sparrowwallet.hummingbird.registry.aptos.AptosSignature;
import com.sparrowwallet.hummingbird.registry.cosmos.CosmosSignRequest;
import com.sparrowwallet.hummingbird.registry.cosmos.CosmosSignature;
import com.sparrowwallet.hummingbird.registry.near.NearSignRequest;
import com.sparrowwallet.hummingbird.registry.near.NearSignature;
import com.sparrowwallet.hummingbird.registry.solana.SolNFTItem;
import com.sparrowwallet.hummingbird.registry.solana.SolSignRequest;
import com.sparrowwallet.hummingbird.registry.solana.SolSignature;

public enum RegistryType {
    BYTES("bytes", null, byte[].class),
    CBOR_PNG("cbor-png", null, null),
    CBOR_SVG("cbor-svg", null, null),
    COSE_SIGN("cose-sign", 98, null),
    COSE_SIGN1("cose-sign1", 18, null),
    COSE_ENCRYPT("cose-encrypt", 96, null),
    COSE_ENCRYPT0("cose-encrypt0", 16, null),
    COSE_MAC("cose-mac", 97, null),
    COSE_MAC0("cose-mac0", 17, null),
    COSE_KEY("cose-key", null, null),
    COSE_KEYSET("cose-keyset", null, null),
    CRYPTO_SEED("crypto-seed", 300, CryptoSeed.class),
    CRYPTO_BIP39("crypto-bip39", 301, CryptoBip39.class),
    CRYPTO_HDKEY("crypto-hdkey", 303, CryptoHDKey.class),
    CRYPTO_KEYPATH("crypto-keypath", 304, CryptoKeypath.class),
    CRYPTO_COIN_INFO("crypto-coin-info", 305, CryptoCoinInfo.class),
    CRYPTO_ECKEY("crypto-eckey", 306, CryptoECKey.class),
    CRYPTO_ADDRESS("crypto-address", 307, CryptoAddress.class),
    CRYPTO_OUTPUT("crypto-output", 308, CryptoOutput.class),
    CRYPTO_SSKR("crypto-sskr", 309, null),
    CRYPTO_PSBT("crypto-psbt", 310, CryptoPSBT.class),
    CRYPTO_ACCOUNT("crypto-account", 311, CryptoAccount.class),

    // self-defined-type
    ETH_SIGN_REQUEST("eth-sign-request",401, EthSignRequest.class),
    ETH_SIGNATURE("eth-signature",402, EthSignature.class),
    ETH_NFT_ITEM("eth-nft-item",403, EthNFTItem.class),

    SOL_SIGN_REQUEST("sol-sign-request", 1101, SolSignRequest.class),
    SOL_SIGNATURE("sol-signature", 1102, SolSignature.class),
    CRYPTO_MULTI_ACCOUNTS("crypto-multi-accounts", 1103, CryptoMultiAccounts.class),
    SOL_NFT_ITEM("sol-nft-item",1104, SolNFTItem.class),

    NEAR_SIGN_REQUEST("near-sign-request", 2101, NearSignRequest.class),
    NEAR_SIGNATURE("near-signature", 2102, NearSignature.class),

    APTOS_SIGN_REQUEST("aptos-sign-request", 3101, AptosSignRequest.class),
    APTOS_SIGNATURE("aptos-signature", 3102, AptosSignature.class),

    COSMOS_SIGN_REQUEST("cosmos-sign-request", 4101, CosmosSignRequest.class),
    COSMOS_SIGNATURE("cosmos-signature", 4102, CosmosSignature.class);

    private final String type;
    private final Integer tag;
    private final Class registryClass;

    private RegistryType(String type, Integer tag, Class registryClass) {
        this.type = type;
        this.tag = tag;
        this.registryClass = registryClass;
    }

    public String getType() {
        return type;
    }

    public Integer getTag() {
        return tag;
    }

    public Class getRegistryClass() {
        return registryClass;
    }

    @Override
    public String toString() {
        return type;
    }

    public static RegistryType fromString(String type) {
        for(RegistryType registryType : values()) {
            if(registryType.toString().equals(type.toLowerCase())) {
                return registryType;
            }
        }

        throw new IllegalArgumentException("Unknown UR registry type: " + type);
    }
}
