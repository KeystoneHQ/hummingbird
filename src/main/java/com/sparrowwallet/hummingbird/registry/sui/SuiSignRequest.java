package com.sparrowwallet.hummingbird.registry.sui;

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

public class SuiSignRequest extends RegistryItem {
    public static final int REQUEST_ID = 1;
    public static final int SIGN_DATA = 2;
    public static final int SIGN_TYPE = 3;
    public static final int DERIVATION_PATHS = 4;
    public static final int ADDRESSES = 5;
    public static final int ORIGIN = 6;

    private final byte[] requestId;
    private final byte[] signData;
    private SignType signType;
    private final List<CryptoKeypath> derivationPaths;
    private final List<byte[]> addresses;
    private final String origin;


    public SuiSignRequest(byte[] signData, Integer signType, List<CryptoKeypath> derivationPaths) {
        this.requestId = null;
        this.signData = signData;
        this.signType = SignType.fromInteger(signType);
        this.derivationPaths = derivationPaths;
        this.addresses = null;
        this.origin = null;
    }


    public SuiSignRequest(byte[] signData, Integer signType, List<CryptoKeypath> derivationPaths, byte[] requestId) {
        this.requestId = requestId;
        this.signData = signData;
        this.signType = SignType.fromInteger(signType);
        this.derivationPaths = derivationPaths;
        this.addresses = null;
        this.origin = null;
    }


    public SuiSignRequest(byte[] signData, Integer signType, List<CryptoKeypath> derivationPaths, byte[] requestId, List<byte[]> addresses, String origin) {
        this.requestId = requestId;
        this.signData = signData;
        this.signType = SignType.fromInteger(signType);
        this.derivationPaths = derivationPaths;
        this.addresses = addresses;
        this.origin = origin;
    }

    public byte[] getRequestId() {
        return requestId;
    }

    public byte[] getSignData() {
        return signData;
    }

    public String[] getDerivationPaths() {
        return derivationPaths.stream().map(item -> item.getPath()).collect(Collectors.toList()).toArray(new String[derivationPaths.size()]);
    }

    public byte[] getMasterFingerprint() {
        return derivationPaths.get(0).getSourceFingerprint();
    }

    public String getDerivationPath(int index) {
        return derivationPaths.get(index).getPath();
    }

    public byte[] getMasterFingerprint(int index) {
        return derivationPaths.get(index).getSourceFingerprint();
    }


    public List<byte[]> getAddresses() {
        return addresses;
    }

    public String getOrigin() {
        return origin;
    }

    public SignType getSignType() {
        return signType;
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.SUI_SIGN_REQUEST;
    }

    @Override
    public DataItem toCbor() {
        Map map = new Map();
        if (requestId != null) {
            DataItem uuid = new ByteString(requestId);
            uuid.setTag(37);
            map.put(new UnsignedInteger(REQUEST_ID), uuid);
        }

        if (addresses != null) {
            Array array = new Array();
            for (byte[] addr : addresses) {
                DataItem x = new ByteString(addr);
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
        map.put(new UnsignedInteger(SIGN_TYPE), new UnsignedInteger(signType.getTypeIndex()));

        return map;
    }

    @Override
    public String toString() {
        return "SuiSignRequest{" +
                "requestId=" + Arrays.toString(requestId) +
                ", signData=" + Arrays.toString(signData) +
                ", signType=" + signType +
                ", derivationPaths=" + derivationPaths +
                ", addresses='" + addresses + '\'' +
                ", origin='" + origin + '\'' +
                '}';
    }

    public static SuiSignRequest fromCbor(DataItem item) {
        byte[] requestId = null;
        byte[] signData = null;
        Integer signTypeIndex = null;
        List<CryptoKeypath> derivationPaths = null;
        List<byte[]> addressList = null;
        String origin = null;

        Map map = (Map) item;
        for (DataItem key : map.getKeys()) {
            UnsignedInteger uintKey = (UnsignedInteger) key;
            int intKey = uintKey.getValue().intValue();
            if (intKey == REQUEST_ID) {
                requestId = ((ByteString) map.get(uintKey)).getBytes();
            } else if (intKey == SIGN_DATA) {
                signData = ((ByteString) map.get(uintKey)).getBytes();
            } else if (intKey == SIGN_TYPE) {
                signTypeIndex = ((UnsignedInteger) map.get(uintKey)).getValue().intValue();
            } else if (intKey == DERIVATION_PATHS) {
                Array derivationPathArray = (Array) map.get(uintKey);
                derivationPaths = new ArrayList<>(derivationPathArray.getDataItems().size());
                for (DataItem derivationPath : derivationPathArray.getDataItems()) {
                    derivationPaths.add(CryptoKeypath.fromCbor(derivationPath));
                }
            } else if (intKey == ADDRESSES) {
                Array addrArray = (Array) map.get(uintKey);
                addressList = new ArrayList<>(addrArray.getDataItems().size());
                for (DataItem addr : addrArray.getDataItems()) {
                    addressList.add(((ByteString) addr).getBytes());
                }
            } else if (intKey == ORIGIN) {
                origin = ((UnicodeString) map.get(uintKey)).getString();
            }
        }
        if (signData == null || derivationPaths == null || signTypeIndex == null) {
            throw new IllegalStateException("required data field is missing");
        }
        return new SuiSignRequest(signData, signTypeIndex, derivationPaths, requestId, addressList, origin);
    }

    public enum SignType {
        SINGLE("sign-type-single", 1),
        MULTI("sign-type-multi", 2),
        MESSAGE("sign-type-message", 3);

        private final String type;
        private final Integer typeIndex;

        SignType(String type, Integer typeIndex) {
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
            throw new IllegalArgumentException("Unknown sui sign type: " + typeIndex);
        }
    }
}
