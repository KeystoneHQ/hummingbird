package com.sparrowwallet.hummingbird.registry.evm;

import com.sparrowwallet.hummingbird.registry.CryptoKeypath;
import com.sparrowwallet.hummingbird.registry.RegistryItem;
import com.sparrowwallet.hummingbird.registry.RegistryType;

import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnicodeString;
import co.nstant.in.cbor.model.UnsignedInteger;

public class EvmSignRequest extends RegistryItem {
    public static final int REQUEST_ID = 1;
    public static final int SIGN_DATA = 2;
    public static final int DATA_TYPE = 3;
    public static final int CUSTOM_CHAIN_IDENTIFIER = 4;
    public static final int DERIVATION_PATH = 5;
    public static final int ADDRESS = 6;
    public static final int ORIGIN = 7;

    private final byte[] requestId;
    private final byte[] signData;
    private final Integer customChainIdentifier;
    private final DataType dataType;
    private final CryptoKeypath derivationPath;
    private final byte[] address;
    private final String origin;

    public EvmSignRequest(byte[] signData, Integer dataType, Integer customChainIdentifier, CryptoKeypath derivationPath, byte[] requestId, byte[] address, String origin) {
        this.requestId = requestId;
        this.signData = signData;
        this.dataType = DataType.fromInteger(dataType);
        this.customChainIdentifier = customChainIdentifier;
        this.derivationPath = derivationPath;
        this.address = address;
        this.origin = origin;
    }

    public byte[] getRequestId() {
        return requestId;
    }

    public byte[] getSignData() {
        return signData;
    }

    public String getDataType() {
        return dataType.getType();
    }

    public String getDerivationPath() {
        return derivationPath.getPath();
    }

    public byte[] getMasterFingerprint() {
        return derivationPath.getSourceFingerprint();
    }

    public byte[] getAddress() {
        return address;
    }

    public int getCustomChainIdentifier() {
        return customChainIdentifier == null ? -1 : customChainIdentifier;
    }

    public String getOrigin() {
        return origin;
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.EVM_SIGN_REQUEST;
    }

    @Override
    public DataItem toCbor() {
        Map map = new Map();
        if (requestId != null) {
            DataItem uuid = new ByteString(requestId);
            uuid.setTag(37);
            map.put(new UnsignedInteger(REQUEST_ID), uuid);
        }
        if (address != null) {
            map.put(new UnsignedInteger(ADDRESS), new ByteString(address));
        }
        if (origin != null) {
            map.put(new UnsignedInteger(ORIGIN), new UnicodeString(origin));
        }
        map.put(new UnsignedInteger(SIGN_DATA), new ByteString(signData));
        map.put(new UnsignedInteger(DATA_TYPE), new UnsignedInteger(dataType.getTypeIndex()));
        map.put(new UnsignedInteger(CUSTOM_CHAIN_IDENTIFIER), new UnsignedInteger(customChainIdentifier));
        DataItem path = derivationPath.toCbor();
        path.setTag(RegistryType.CRYPTO_KEYPATH.getTag());
        map.put(new UnsignedInteger(DERIVATION_PATH), path);
        return map;
    }

    public static EvmSignRequest fromCbor(DataItem item) {
        byte[] requestId = null;
        byte[] signData = null;
        Integer dataTypeIndex = null;
        Integer customChainIdentifer = null;
        CryptoKeypath derivationPath = null;
        byte[] address = null;
        String origin = null;

        Map map = (Map) item;
        for (DataItem key : map.getKeys()) {
            UnsignedInteger uintKey = (UnsignedInteger) key;
            int intKey = uintKey.getValue().intValue();
            if (intKey == REQUEST_ID) {
                requestId = ((ByteString) map.get(uintKey)).getBytes();
            } else if (intKey == SIGN_DATA) {
                signData = ((ByteString) map.get(uintKey)).getBytes();
            } else if (intKey == DATA_TYPE) {
                dataTypeIndex = ((UnsignedInteger) map.get(uintKey)).getValue().intValue();
            } else if (intKey == CUSTOM_CHAIN_IDENTIFIER) {
                customChainIdentifer = ((UnsignedInteger) map.get(uintKey)).getValue().intValue();
            } else if (intKey == DERIVATION_PATH) {
                derivationPath = CryptoKeypath.fromCbor(map.get(uintKey));
            } else if (intKey == ADDRESS) {
                address = ((ByteString) map.get(uintKey)).getBytes();
            } else if (intKey == ORIGIN) {
                origin = ((UnicodeString) map.get(uintKey)).getString();
            }
        }

        if (signData == null || dataTypeIndex == null || derivationPath == null) {
            throw new IllegalStateException("required data field is missing");
        }

        return new EvmSignRequest(signData, dataTypeIndex, customChainIdentifer, derivationPath, requestId, address, origin);
    }


    public enum DataType {

        ARBITRARY_TRANSACTION("evm-arbitrary-data", 1),
        AMINO_TRANSACTION("evm-amino-transaction", 2),
        DIRECT_TRANSACTION("evm-direct-transaction", 3);

        private final String type;
        private final Integer typeIndex;

        DataType(String type, Integer typeIndex) {
            this.type = type;
            this.typeIndex = typeIndex;

        }

        public String getType() {
            return type;
        }

        public Integer getTypeIndex() {
            return typeIndex;
        }

        @Override
        public String toString() {
            return type;
        }

        public static DataType fromInteger(Integer typeIndex) {
            for (DataType dataType : DataType.values()) {
                if (dataType.getTypeIndex().equals(typeIndex)) {
                    return dataType;
                }
            }
            throw new IllegalArgumentException("Unknown evm data type: " + typeIndex);
        }
    }
}
