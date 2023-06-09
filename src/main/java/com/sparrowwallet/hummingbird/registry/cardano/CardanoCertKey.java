package com.sparrowwallet.hummingbird.registry.cardano;

import com.sparrowwallet.hummingbird.registry.CryptoKeypath;
import com.sparrowwallet.hummingbird.registry.RegistryItem;
import com.sparrowwallet.hummingbird.registry.RegistryType;

import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnsignedInteger;


public class CardanoCertKey extends RegistryItem {
    private static final int KEY_HASH = 1;
    private final static int KEY_PATH = 2;

    private final byte[] keyHash;
    private final CryptoKeypath keypath;

    public CardanoCertKey(byte[] keyHash, CryptoKeypath keypath) {
        this.keyHash = keyHash;
        this.keypath = keypath;
    }

    public byte[] getKeyHash() {
        return keyHash;
    }

    public CryptoKeypath getKeypath() {
        return keypath;
    }


    @Override
    public DataItem toCbor() {
        Map map = new Map();
        map.put(new UnsignedInteger(KEY_HASH), new ByteString(keyHash));

        DataItem keypath = this.keypath.toCbor();
        keypath.setTag(this.keypath.getRegistryType().getTag());
        map.put(new UnsignedInteger(KEY_PATH), keypath);
        return map;
    }

    public static CardanoCertKey fromCbor(DataItem item) {
        byte[] keyhash = null;
        CryptoKeypath path = null;
        Map map = (Map) item;

        for (DataItem key : map.getKeys()) {
            UnsignedInteger uintKey = (UnsignedInteger) key;
            int intKey = uintKey.getValue().intValue();
            if (intKey == KEY_HASH) {
                keyhash = ((ByteString) map.get(uintKey)).getBytes();
            }
            if (intKey == KEY_PATH) {
                path = CryptoKeypath.fromCbor(map.get(uintKey));
            }
        }

        return new CardanoCertKey(keyhash, path);
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.CARDANO_CERT_KEY;
    }
}
