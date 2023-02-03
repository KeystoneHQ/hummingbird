package com.sparrowwallet.hummingbird.registry.arweave;

import com.sparrowwallet.hummingbird.registry.RegistryItem;
import com.sparrowwallet.hummingbird.registry.RegistryType;

import java.math.BigInteger;
import java.util.Arrays;

import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnicodeString;
import co.nstant.in.cbor.model.UnsignedInteger;

public class ArweaveSignRequest extends RegistryItem {
    public static final int MASTER_FINGERPRINT = 1;
    public static final int REQUEST_ID = 2;
    public static final int SIGN_DATA = 3;
    public static final int SIGN_TYPE = 4;
    public static final int SALT_LEN = 5;
    public static final int ORIGIN = 6;
    public static final int ACCOUNT = 7;

    private final byte[] masterFingerprint;
    private final byte[] requestId;
    private final byte[] signData;

    public ArweaveSignRequest(byte[] masterFingerprint, byte[] requestId, byte[] signData, SignType signType, SaltLen saltLen, String origin, byte[] account) {
        this.masterFingerprint = masterFingerprint;
        this.requestId = requestId;
        this.signData = signData;
        this.signType = signType;
        this.saltLen = saltLen;
        this.origin = origin;
        this.account = account;
    }

    private final SignType signType;
    private final SaltLen saltLen;
    private final String origin;
    private final byte[] account;

    @Override
    public DataItem toCbor() {
        Map map = new Map();
        map.put(new UnsignedInteger(MASTER_FINGERPRINT), new UnsignedInteger(new BigInteger(1, masterFingerprint)));

        if (requestId != null) {
            DataItem uuid = new ByteString(requestId);
            uuid.setTag(37);
            map.put(new UnsignedInteger(REQUEST_ID), uuid);
        }
        map.put(new UnsignedInteger(SIGN_DATA), new ByteString(signData));
        map.put(new UnsignedInteger(SIGN_TYPE), new UnsignedInteger(signType.getTypeIndex()));
        map.put(new UnsignedInteger(SALT_LEN), new UnsignedInteger(saltLen.getLength()));
        map.put(new UnsignedInteger(ORIGIN), new UnicodeString(origin));
        map.put(new UnsignedInteger(ACCOUNT), new ByteString(account));
        return map;
    }

    public static ArweaveSignRequest fromCbor(DataItem item) {
        byte[] masterFingerprint = null;
        byte[] requestId = null;
        byte[] signData = null;
        Integer signTypeIndex = null;
        Integer saltLen = null;
        String origin = null;
        byte[] account = null;

        Map map = (Map) item;
        for (DataItem key : map.getKeys()) {
            UnsignedInteger uintKey = (UnsignedInteger) key;
            int intKey = uintKey.getValue().intValue();
            if (intKey == MASTER_FINGERPRINT) {
                byte[] mfp = ((UnsignedInteger) map.get(uintKey)).getValue().toByteArray();
                masterFingerprint = Arrays.copyOfRange(mfp, mfp.length - 4, mfp.length);
            } else if (intKey == REQUEST_ID) {
                requestId = ((ByteString) map.get(uintKey)).getBytes();
            } else if (intKey == SIGN_DATA) {
                signData = ((ByteString) map.get(uintKey)).getBytes();
            } else if (intKey == SIGN_TYPE) {
                signTypeIndex = ((UnsignedInteger) map.get(uintKey)).getValue().intValue();
            } else if (intKey == SALT_LEN) {
                saltLen = ((UnsignedInteger) map.get(uintKey)).getValue().intValue();
            } else if (intKey == ORIGIN) {
                origin = ((UnicodeString) map.get(uintKey)).getString();
            } else if (intKey == ACCOUNT) {
                account = ((ByteString) map.get(uintKey)).getBytes();
            }
        }

        if (signData == null || signTypeIndex == null || saltLen == null || masterFingerprint == null) {
            throw new IllegalStateException("required data field is missing");
        }

        return new ArweaveSignRequest(masterFingerprint, requestId, signData, SignType.fromInteger(signTypeIndex), SaltLen.fromInteger(saltLen), origin, account);
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.ARWEAVE_SIGN_REQUEST;
    }

    public byte[] getMasterFingerprint() {
        return masterFingerprint;
    }

    public byte[] getRequestId() {
        return requestId;
    }

    public byte[] getSignData() {
        return signData;
    }

    public SignType getSignType() {
        return signType;
    }

    public SaltLen getSaltLen() {
        return saltLen;
    }

    public String getOrigin() {
        return origin;
    }

    public byte[] getAccount() {
        return account;
    }

    public enum SignType {
        TRANSACTION("Transaction", 1),
        DATAITEM("DataItem", 2),
        MESSAGE("Message", 3);

        private final String type;
        private final Integer typeIndex;

        private SignType(String type, Integer typeIndex) {
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

        public static SignType fromInteger(Integer typeIndex) {
            for (SignType dataType : SignType.values()) {
                if (dataType.getTypeIndex().equals(typeIndex)) {
                    return dataType;
                }
            }

            throw new IllegalArgumentException("Unknown sign type: " + typeIndex);
        }
    }

    public enum SaltLen {
        ZERO("Zero", 0),
        DIGEST("Digest", 32);

        private String type;
        private int length;

        private SaltLen(String type, Integer length) {
            this.type = type;
            this.length = length;
        }

        public String getType() {
            return type;
        }

        public int getLength() {
            return length;
        }

        public static SaltLen fromInteger(Integer length) {
            for (SaltLen dataType : SaltLen.values()) {
                if (dataType.getLength() == length) {
                    return dataType;
                }
            }

            throw new IllegalArgumentException("Unknown length: " + length);
        }
    }
}
