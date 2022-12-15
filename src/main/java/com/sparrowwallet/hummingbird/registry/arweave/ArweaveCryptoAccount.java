package com.sparrowwallet.hummingbird.registry.arweave;

import com.sparrowwallet.hummingbird.registry.RegistryItem;
import com.sparrowwallet.hummingbird.registry.RegistryType;

import java.math.BigInteger;

import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnicodeString;
import co.nstant.in.cbor.model.UnsignedInteger;

public class ArweaveCryptoAccount extends RegistryItem {
    private final static int MASTER_FINGERPRINT_KEY = 1;
    private final static int KEY_DATA_KEY = 2;
    private final static int DEVICE_KEY = 3;

    private final byte[] masterFingerprint;
    private final byte[] keyData;
    private final String device;

    @Override
    public DataItem toCbor() {
        Map map = new Map();
        map.put(new UnsignedInteger(MASTER_FINGERPRINT_KEY), new UnsignedInteger(new BigInteger(1, masterFingerprint)));
        map.put(new UnsignedInteger(KEY_DATA_KEY), new ByteString(keyData));
        if (this.device != null)
            map.put(new UnsignedInteger(DEVICE_KEY), new UnicodeString(device));
        return map;
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.ARWEAVE_CRYPTO_ACCOUNT;
    }

    public ArweaveCryptoAccount(byte[] masterFingerprint, byte[] keyData, String device) {
        this.masterFingerprint = masterFingerprint;
        this.keyData = keyData;
        this.device = device;
    }

    public static ArweaveCryptoAccount fromCbor(DataItem item) {
        byte[] masterFingerprint = null;
        byte[] keyData = null;
        String device = null;

        Map map = (Map) item;
        for (DataItem key : map.getKeys()) {
            UnsignedInteger uintKey = (UnsignedInteger) key;
            int intKey = uintKey.getValue().intValue();
            if (intKey == MASTER_FINGERPRINT_KEY) {
                masterFingerprint = ((UnsignedInteger) map.get(uintKey)).getValue().toByteArray();
            } else if (intKey == KEY_DATA_KEY) {
                keyData = ((ByteString) map.get(uintKey)).getBytes();
            } else if (intKey == DEVICE_KEY) {
                device = ((UnicodeString) map.get(uintKey)).getString();
            }
        }

        if (masterFingerprint == null || keyData == null) {
            throw new IllegalStateException("Invalid data");
        }
        return new ArweaveCryptoAccount(masterFingerprint, keyData, device);
    }

    public byte[] getMasterFingerprint() {
        return masterFingerprint;
    }

    public byte[] getKeyData() {
        return keyData;
    }

    public String getDevice() {
        return device;
    }
}
