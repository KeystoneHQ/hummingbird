package com.sparrowwallet.hummingbird.registry;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import co.nstant.in.cbor.model.Array;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnicodeString;
import co.nstant.in.cbor.model.UnsignedInteger;

public class CryptoMultiAccounts extends RegistryItem {
    public static final int MASTER_FINGERPRINT = 1;
    public static final int KEYS = 2;
    public static final int ORIGIN = 3;

    private final byte[] masterFingerprint;
    private final List<CryptoHDKey> keys;
    private final String origin;

    public CryptoMultiAccounts(byte[] masterFingerprint, List<CryptoHDKey> keys) {
        this.masterFingerprint = masterFingerprint;
        this.keys = keys;
        this.origin = null;
    }

    public CryptoMultiAccounts(byte[] masterFingerprint, List<CryptoHDKey> keys, String origin) {
        this.masterFingerprint = masterFingerprint;
        this.keys = keys;
        this.origin = origin;
    }

    public byte[] getMasterFingerprint() {
        return masterFingerprint;
    }

    public String getOrigin() {
        return origin;
    }

    public List<CryptoHDKey> getKeys() {
        return keys;
    }

    @Override
    public DataItem toCbor() {
        Map map = new Map();
        map.put(new UnsignedInteger(MASTER_FINGERPRINT), new UnsignedInteger(new BigInteger(1, masterFingerprint)));
        Array array = new Array();
        for (CryptoHDKey cryptoHDKey : keys) {
            DataItem x = cryptoHDKey.toCbor();
            x.setTag(cryptoHDKey.getRegistryType().getTag());
            array.add(x);
        }
        map.put(new UnsignedInteger(KEYS), array);
        if (origin != null) {
            map.put(new UnsignedInteger(ORIGIN), new UnicodeString(origin));
        }
        return map;
    }

    public static CryptoMultiAccounts fromCbor(DataItem dataItem) {
        Map item = (Map) dataItem;
        String origin = null;

        UnsignedInteger uintMasterFingerprint = (UnsignedInteger) item.get(new UnsignedInteger(MASTER_FINGERPRINT));
        Array keys = (Array) item.get(new UnsignedInteger(KEYS));
        List<CryptoHDKey> cryptoHDKeys = new ArrayList<>(keys.getDataItems().size());
        for (DataItem key : keys.getDataItems()) {
            cryptoHDKeys.add(CryptoHDKey.fromCbor(key));
        }
        if (item.get(new UnsignedInteger(ORIGIN)) != null) {
            origin = item.get(new UnsignedInteger(ORIGIN)).toString();
        }

        return new CryptoMultiAccounts(uintMasterFingerprint.getValue().toByteArray(), cryptoHDKeys, origin);
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.CRYPTO_MULTI_ACCOUNTS;
    }


}
