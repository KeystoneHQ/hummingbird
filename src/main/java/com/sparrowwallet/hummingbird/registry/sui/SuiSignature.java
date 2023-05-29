package com.sparrowwallet.hummingbird.registry.sui;

import com.sparrowwallet.hummingbird.registry.RegistryItem;
import com.sparrowwallet.hummingbird.registry.RegistryType;

import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnsignedInteger;

public class SuiSignature extends RegistryItem {
    public static final int REQUEST_ID = 1;
    public static final int SIGNATURE = 2;
    public static final int PUBLIC_KEY = 3;


    private final byte[] requestId;
    private final byte[] signature;
    private final byte[] publicKey;

    public SuiSignature(byte[] signature) {
        this.requestId = null;
        this.signature = signature;
        this.publicKey = null;
    }

    public SuiSignature(byte[] signature, byte[] requestId) {
        this.requestId = requestId;
        this.signature = signature;
        this.publicKey = null;
    }

    public SuiSignature(byte[] signature, byte[] requestId, byte[] publicKey) {
        this.requestId = requestId;
        this.signature = signature;
        this.publicKey = publicKey;
    }

    public byte[] getRequestId() {
        return requestId;
    }

    public byte[] getSignature() {
        return signature;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.SUI_SIGNATURE;
    }

    @Override
    public DataItem toCbor() {
        Map map = new Map();
        if (requestId != null) {
            DataItem uuid = new ByteString(requestId);
            uuid.setTag(37);
            map.put(new UnsignedInteger(REQUEST_ID), uuid);
        }
        map.put(new UnsignedInteger(SIGNATURE), new ByteString(signature));
        if (publicKey != null) {
            map.put(new UnsignedInteger(PUBLIC_KEY), new ByteString(publicKey));
        }
        return map;
    }

    public static SuiSignature fromCbor(DataItem item) {
        byte[] requestId = null;
        byte[] signature = null;
        byte[] publicKey = null;

        Map map = (Map) item;
        for (DataItem key : map.getKeys()) {
            UnsignedInteger uintKey = (UnsignedInteger) key;
            int intKey = uintKey.getValue().intValue();
            if (intKey == REQUEST_ID) {
                requestId = ((ByteString) map.get(uintKey)).getBytes();
            } else if (intKey == SIGNATURE) {
                signature = ((ByteString) map.get(uintKey)).getBytes();
            } else if (intKey == PUBLIC_KEY) {
                publicKey = ((ByteString) map.get(uintKey)).getBytes();
            }
        }

        if (signature == null) {
            throw new IllegalStateException("required data field is missing");
        }
        return new SuiSignature(signature, requestId, publicKey);
    }
}
