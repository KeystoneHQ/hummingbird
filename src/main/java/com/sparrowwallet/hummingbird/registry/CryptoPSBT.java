package com.sparrowwallet.hummingbird.registry;

import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;

public class CryptoPSBT {
    private final byte[] psbt;

    public CryptoPSBT(byte[] psbt) {
        this.psbt = psbt;
    }

    public byte[] getPsbt() {
        return psbt;
    }

    public static CryptoPSBT fromCbor(DataItem item) {
        return new CryptoPSBT(((ByteString)item).getBytes());
    }
}
