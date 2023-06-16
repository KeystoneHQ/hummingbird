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
    public static final int INTENT_MESSAGE = 2;
    public static final int DERIVATION_PATHS = 3;
    public static final int ADDRESSES = 4;
    public static final int ORIGIN = 5;

    private final byte[] requestId;
    private final byte[] intentMessage;
    private final List<CryptoKeypath> derivationPaths;
    private final List<byte[]> addresses;
    private final String origin;


    public SuiSignRequest(byte[] intentMessage, List<CryptoKeypath> derivationPaths) {
        this.requestId = null;
        this.intentMessage = intentMessage;
        this.derivationPaths = derivationPaths;
        this.addresses = null;
        this.origin = null;
    }


    public SuiSignRequest(byte[] intentMessage, List<CryptoKeypath> derivationPaths, byte[] requestId) {
        this.requestId = requestId;
        this.intentMessage = intentMessage;
        this.derivationPaths = derivationPaths;
        this.addresses = null;
        this.origin = null;
    }


    public SuiSignRequest(byte[] intentMessage, List<CryptoKeypath> derivationPaths, byte[] requestId, List<byte[]> addresses, String origin) {
        this.requestId = requestId;
        this.intentMessage = intentMessage;
        this.derivationPaths = derivationPaths;
        this.addresses = addresses;
        this.origin = origin;
    }

    public byte[] getRequestId() {
        return requestId;
    }

    public byte[] getIntentMessage() {
        return intentMessage;
    }

    public String[] getDerivationPaths() {
        return derivationPaths.stream().map(CryptoKeypath::getPath).collect(Collectors.toList()).toArray(new String[derivationPaths.size()]);
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

        map.put(new UnsignedInteger(INTENT_MESSAGE), new ByteString(intentMessage));

        Array array = new Array();
        for (CryptoKeypath keyPath : derivationPaths) {
            DataItem x = keyPath.toCbor();
            x.setTag(RegistryType.CRYPTO_KEYPATH.getTag());
            array.add(x);
        }
        map.put(new UnsignedInteger(DERIVATION_PATHS), array);

        return map;
    }

    @Override
    public String toString() {
        return "SuiSignRequest{" +
                "requestId=" + Arrays.toString(requestId) +
                ", intentMessage=" + Arrays.toString(intentMessage) +
                ", derivationPaths=" + derivationPaths +
                ", addresses='" + addresses + '\'' +
                ", origin='" + origin + '\'' +
                '}';
    }

    public static SuiSignRequest fromCbor(DataItem item) {
        byte[] requestId = null;
        byte[] intentMessage = null;
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
            } else if (intKey == INTENT_MESSAGE) {
                intentMessage = ((ByteString) map.get(uintKey)).getBytes();
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
        if (intentMessage == null || derivationPaths == null || signTypeIndex == null) {
            throw new IllegalStateException("required data field is missing");
        }
        return new SuiSignRequest(intentMessage, derivationPaths, requestId, addressList, origin);
    }
}
