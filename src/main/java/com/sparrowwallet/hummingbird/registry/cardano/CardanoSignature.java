package com.sparrowwallet.hummingbird.registry.cardano;

import com.sparrowwallet.hummingbird.registry.RegistryItem;
import com.sparrowwallet.hummingbird.registry.RegistryType;

import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnsignedInteger;

public class CardanoSignature extends RegistryItem {
    private static final int REQUEST_ID = 1;
    private final static int WITNESS_SET = 2;

    private final byte[] requestId;
    private final byte[] witnessSet;

    public CardanoSignature(byte[] witnessSet) {
        this.witnessSet = witnessSet;
        this.requestId = null;
    }

    public CardanoSignature(byte[] requestId, byte[] witnessSet) {
        this.requestId = requestId;
        this.witnessSet = witnessSet;
    }

    public byte[] getRequestId() {
        return requestId;
    }

    public byte[] getWitnessSet() {
        return witnessSet;
    }

    @Override
    public DataItem toCbor() {
        Map map = new Map();
        if (requestId != null) {
            DataItem uuid = new ByteString(requestId);
            uuid.setTag(37);
            map.put(new UnsignedInteger(REQUEST_ID), uuid);
        }
        map.put(new UnsignedInteger(WITNESS_SET), new ByteString(witnessSet));
        return map;
    }

    public static CardanoSignature fromCbor(DataItem item) {
        byte[] requestId = null;
        byte[] witnessSet = null;
        Map map = (Map) item;

        for (DataItem key : map.getKeys()) {
            UnsignedInteger uintKey = (UnsignedInteger) key;
            int intKey = uintKey.getValue().intValue();
            if (intKey == REQUEST_ID) {
                requestId = ((ByteString) map.get(uintKey)).getBytes();
            }
            if (intKey == WITNESS_SET) {
                witnessSet = ((ByteString) map.get(uintKey)).getBytes();
            }
        }

        if (witnessSet == null) {
            throw new IllegalArgumentException("Invalid Cardano signature, witness set should not be null");
        }

        return new CardanoSignature(requestId, witnessSet);
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.CARDANO_SIGNATURE;
    }
}
