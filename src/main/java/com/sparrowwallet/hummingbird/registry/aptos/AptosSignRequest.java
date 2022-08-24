package com.sparrowwallet.hummingbird.registry.aptos;

import com.sparrowwallet.hummingbird.registry.CryptoKeypath;
import com.sparrowwallet.hummingbird.registry.RegistryItem;
import com.sparrowwallet.hummingbird.registry.RegistryType;

import java.util.ArrayList;
import java.util.List;

import co.nstant.in.cbor.model.Array;
import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnicodeString;
import co.nstant.in.cbor.model.UnsignedInteger;

public class AptosSignRequest extends RegistryItem {
    public static final int REQUEST_ID = 1;
    public static final int SIGN_DATA = 2;
    public static final int AUTHENTICATION_KEY_DERIVATION_PATHS = 3;
    public static final int ACCOUNT = 4;
    public static final int ORIGIN = 5;
    public static final int TYPE = 6;

    private final byte[] requestId;
    private final byte[] signData;
    private final List<CryptoKeypath> authenticationKeyDerivationPaths;
    private final String account;
    private final String origin;
    private DataType type;


    public AptosSignRequest(byte[] signData, List<CryptoKeypath> authenticationKeyDerivationPaths, Integer type) {
        this.requestId = null;
        this.signData = signData;
        this.authenticationKeyDerivationPaths = authenticationKeyDerivationPaths;
        this.account = null;
        this.origin = null;
        this.type = DataType.fromInteger(type);
    }


    public AptosSignRequest(byte[] signData, List<CryptoKeypath> authenticationKeyDerivationPaths, byte[] requestId, Integer type) {
        this.requestId = requestId;
        this.signData = signData;
        this.authenticationKeyDerivationPaths = authenticationKeyDerivationPaths;
        this.account = null;
        this.origin = null;
        this.type = DataType.fromInteger(type);
    }


    public AptosSignRequest(byte[] signData, List<CryptoKeypath> authenticationKeyDerivationPaths, byte[] requestId, String account, String origin, Integer type) {
        this.requestId = requestId;
        this.signData = signData;
        this.authenticationKeyDerivationPaths = authenticationKeyDerivationPaths;
        this.account = account;
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
        return authenticationKeyDerivationPaths.get(0).getPath();
    }

    public byte[] getMasterFingerprint() {
        return authenticationKeyDerivationPaths.get(0).getSourceFingerprint();
    }

    public String getDerivationPath(int index) {
        return authenticationKeyDerivationPaths.get(index).getPath();
    }

    public byte[] getMasterFingerprint(int index) {
        return authenticationKeyDerivationPaths.get(index).getSourceFingerprint();
    }


    public String getAccount() {
        return account;
    }

    public String getOrigin() {
        return origin;
    }

    public DataType getType() {
        return type;
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.APTOS_SIGN_REQUEST;
    }

    @Override
    public DataItem toCbor() {
        Map map = new Map();
        if (requestId != null) {
            DataItem uuid = new ByteString(requestId);
            uuid.setTag(37);
            map.put(new UnsignedInteger(REQUEST_ID), uuid);
        }

        if (account != null) {
            map.put(new UnsignedInteger(ACCOUNT), new UnicodeString(account));
        }

        if (origin != null) {
            map.put(new UnsignedInteger(ORIGIN), new UnicodeString(origin));
        }

        map.put(new UnsignedInteger(SIGN_DATA), new ByteString(signData));

        Array array = new Array();
        for (CryptoKeypath keyPath : authenticationKeyDerivationPaths) {
            DataItem x = keyPath.toCbor();
            x.setTag(RegistryType.CRYPTO_KEYPATH.getTag());
            array.add(x);
        }
        map.put(new UnsignedInteger(AUTHENTICATION_KEY_DERIVATION_PATHS), array);
        map.put(new UnsignedInteger(TYPE), new UnsignedInteger(type.getTypeIndex()));

        return map;
    }

    public static AptosSignRequest fromCbor(DataItem item) {
        byte[] requestId = null;
        byte[] signData = null;
        List<CryptoKeypath> authenticationKeyDerivationPaths = null;
        String account = null;
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
            } else if (intKey == AUTHENTICATION_KEY_DERIVATION_PATHS) {
                Array derivationPathArray = (Array) map.get(uintKey);
                authenticationKeyDerivationPaths = new ArrayList<>(derivationPathArray.getDataItems().size());
                for (DataItem derivationPath : derivationPathArray.getDataItems()) {
                    authenticationKeyDerivationPaths.add(CryptoKeypath.fromCbor(derivationPath));
                }
            } else if (intKey == ACCOUNT) {
                account = ((UnicodeString) map.get(uintKey)).getString();
            } else if (intKey == ORIGIN) {
                origin = ((UnicodeString) map.get(uintKey)).getString();
            } else if (intKey == TYPE) {
                dataTypeIndex = ((UnsignedInteger) map.get(uintKey)).getValue().intValue();
            }
        }
        if (signData == null || authenticationKeyDerivationPaths == null || dataTypeIndex == null) {
            throw new IllegalStateException("required data field is missing");
        }
        return new AptosSignRequest(signData, authenticationKeyDerivationPaths, requestId, account, origin, dataTypeIndex);
    }

    public enum DataType {
        SINGLE("sign-type-single", 1),
        MULTI("sign-type-multi", 2),
        MESSAGE("sign-type-message", 3);

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
            throw new IllegalArgumentException("Unknown aptos data type: " + typeIndex);
        }
    }
}
