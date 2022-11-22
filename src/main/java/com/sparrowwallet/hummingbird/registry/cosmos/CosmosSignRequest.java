package com.sparrowwallet.hummingbird.registry.cosmos;

import com.sparrowwallet.hummingbird.registry.CryptoKeypath;
import com.sparrowwallet.hummingbird.registry.RegistryItem;
import com.sparrowwallet.hummingbird.registry.RegistryType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import co.nstant.in.cbor.model.Array;
import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnicodeString;
import co.nstant.in.cbor.model.UnsignedInteger;

public class CosmosSignRequest extends RegistryItem {
    public static final int REQUEST_ID = 1;
    public static final int SIGN_DATA = 2;
    public static final int TYPE = 3;
    public static final int DERIVATION_PATHS = 4;
    public static final int ADDRESSES = 5;
    public static final int ORIGIN = 6;

    private final byte[] requestId;
    private final byte[] signData;
    private DataType type;

    private final List<CryptoKeypath> derivationPaths;
    private final List<String> addressses;
    private final String origin;

    public CosmosSignRequest(byte[] signData, List<CryptoKeypath> derivationPaths, Integer type) {
        this.requestId = null;
        this.signData = signData;
        this.derivationPaths = derivationPaths;
        this.addressses = null;
        this.origin = null;
        this.type = DataType.fromInteger(type);
    }


    public CosmosSignRequest(byte[] signData, List<CryptoKeypath> derivationPaths, byte[] requestId, Integer type) {
        this.requestId = requestId;
        this.signData = signData;
        this.derivationPaths = derivationPaths;
        this.addressses = null;
        this.origin = null;
        this.type = DataType.fromInteger(type);
    }


    public CosmosSignRequest(byte[] signData, List<CryptoKeypath> derivationPaths, byte[] requestId, List<String> addressses, String origin, Integer type) {
        this.requestId = requestId;
        this.signData = signData;
        this.derivationPaths = derivationPaths;
        this.addressses = addressses;
        this.origin = origin;
        this.type = DataType.fromInteger(type);
    }

    public byte[] getRequestId() {
        return requestId;
    }

    public byte[] getSignData() {
        return signData;
    }

    public String getDerivationPath() {
        return derivationPaths.get(0).getPath();
    }

    public String[] getDerivationPaths() {
       return derivationPaths.stream().map(CryptoKeypath::getPath).toArray(String[]::new);
    }

    public byte[] getMasterFingerprint() {
        return derivationPaths.get(0).getSourceFingerprint();
    }

    public List<byte[]> getMasterFingerprints() {
        return derivationPaths.stream().map(CryptoKeypath::getSourceFingerprint).collect(Collectors.toList());

    }

    public String getAddress() {
        return addressses.get(0);
    }

    public String[] getAddresses() {
        return addressses.toArray(new String[0]);
    }

    public String getAddress(int index) {
        return addressses.get(index);
    }

    public String getOrigin() {
        return origin;
    }

    public DataType getType() {
        return type;
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.COSMOS_SIGN_REQUEST;
    }

    @Override
    public DataItem toCbor() {
        Map map = new Map();
        if (requestId != null) {
            DataItem uuid = new ByteString(requestId);
            uuid.setTag(37);
            map.put(new UnsignedInteger(REQUEST_ID), uuid);
        }

        if (addressses != null) {
            Array array = new Array();
            for (String address : addressses) {
                DataItem x = new UnicodeString(address);
                array.add(x);
            }
            map.put(new UnsignedInteger(ADDRESSES), array);
        }

        if (origin != null) {
            map.put(new UnsignedInteger(ORIGIN), new UnicodeString(origin));
        }

        map.put(new UnsignedInteger(SIGN_DATA), new ByteString(signData));

        Array array = new Array();
        for (CryptoKeypath keyPath : derivationPaths) {
            DataItem x = keyPath.toCbor();
            x.setTag(RegistryType.CRYPTO_KEYPATH.getTag());
            array.add(x);
        }
        map.put(new UnsignedInteger(DERIVATION_PATHS), array);
        map.put(new UnsignedInteger(TYPE), new UnsignedInteger(type.getTypeIndex()));

        return map;
    }

    @Override
    public String toString() {
        return "CosmosSignRequest{" +
                "requestId=" + Arrays.toString(requestId) +
                ", signData=" + Arrays.toString(signData) +
                ", derivationPaths=" + derivationPaths +
                ", addresses='" + addressses + '\'' +
                ", origin='" + origin + '\'' +
                ", type=" + type +
                '}';
    }

    public static CosmosSignRequest fromCbor(DataItem item) {
        byte[] requestId = null;
        byte[] signData = null;
        List<CryptoKeypath> derivationPaths = null;
        List<String> addressList = null;
        String origin = null;
        Integer dataTypeIndex = null;

        Map map = (Map) item;
        for (DataItem key : map.getKeys()) {
            UnsignedInteger uintKey = (UnsignedInteger) key;
            int intKey = uintKey.getValue().intValue();
            if (intKey == REQUEST_ID) {
                requestId = ((ByteString) map.get(uintKey)).getBytes();
            } else if (intKey == SIGN_DATA) {
                signData = ((ByteString) map.get(uintKey)).getBytes();
            } else if (intKey == DERIVATION_PATHS) {
                Array derivationPathArray = (Array) map.get(uintKey);
                derivationPaths = new ArrayList<>(derivationPathArray.getDataItems().size());
                for (DataItem derivationPath : derivationPathArray.getDataItems()) {
                    derivationPaths.add(CryptoKeypath.fromCbor(derivationPath));
                }
            } else if (intKey == ADDRESSES) {
                Array addressArray = (Array) map.get(uintKey);
                addressList = new ArrayList<>(addressArray.getDataItems().size());
                for (DataItem address : addressArray.getDataItems()) {
                    addressList.add(((UnicodeString) address).getString());
                }
            } else if (intKey == ORIGIN) {
                origin = ((UnicodeString) map.get(uintKey)).getString();
            } else if (intKey == TYPE) {
                dataTypeIndex = ((UnsignedInteger) map.get(uintKey)).getValue().intValue();
            }
        }
        if (signData == null || derivationPaths == null || dataTypeIndex == null) {
            throw new IllegalStateException("required data field is missing");
        }
        return new CosmosSignRequest(signData, derivationPaths, requestId, addressList, origin, dataTypeIndex);
    }

    public enum DataType {
        AMINO("sign-type-amino", 1),
        DIRECT("sign-type-direct", 2),
        TEXTUAL("sign-type-textual", 3),
        MESSAGE("sign-type-message", 4);

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
            throw new IllegalArgumentException("Unknown data type: " + typeIndex);
        }
    }
}
