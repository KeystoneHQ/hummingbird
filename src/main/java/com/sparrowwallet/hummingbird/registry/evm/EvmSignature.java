package com.sparrowwallet.hummingbird.registry.evm;

import com.sparrowwallet.hummingbird.registry.RegistryItem;
import com.sparrowwallet.hummingbird.registry.RegistryType;

import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnsignedInteger;

public class EvmSignature extends RegistryItem {
    public static final int REQUEST_ID = 1;
    public static final int SIGNATURE = 2;

    private final byte[] requestId;
    private final byte[] signature;

    public EvmSignature(byte[] signature) {
        this.requestId = null;
        this.signature = signature;
    }

    public EvmSignature(byte[] signature, byte[] requestId) {
        this.requestId = requestId;
        this.signature = signature;
    }

    public byte[] getRequestId() {
        return requestId;
    }

    public byte[] getSignature() {
        return signature;
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.EVM_SIGNATURE;
    }

    @Override
    public DataItem toCbor() {
        Map map = new Map();
        if(requestId != null) {
            DataItem uuid = new ByteString(requestId);
            uuid.setTag(37);
            map.put(new UnsignedInteger(REQUEST_ID), uuid);
        }

        map.put(new UnsignedInteger(SIGNATURE), new ByteString(signature));
        return map;
    }

    public static EvmSignature fromCbor(DataItem item) {
        byte[] requestId = null;
        byte[] signature = null;

        Map map = (Map)item;
        for(DataItem key : map.getKeys()) {
            UnsignedInteger uintKey = (UnsignedInteger) key;
            int intKey = uintKey.getValue().intValue();
            if (intKey == REQUEST_ID) {
                requestId = ((ByteString) map.get(uintKey)).getBytes();
            } else if (intKey == SIGNATURE) {
                signature = ((ByteString) map.get(uintKey)).getBytes();
            }
        }

        if(signature == null) {
            throw new IllegalStateException("required data field is missing");
        }

        if(requestId != null) {
            return new EvmSignature(signature, requestId);
        } else {
            return new EvmSignature(signature);
        }
    }
}
