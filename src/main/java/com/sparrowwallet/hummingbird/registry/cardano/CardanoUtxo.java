package com.sparrowwallet.hummingbird.registry.cardano;

import com.sparrowwallet.hummingbird.registry.CryptoKeypath;
import com.sparrowwallet.hummingbird.registry.RegistryItem;
import com.sparrowwallet.hummingbird.registry.RegistryType;

import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnicodeString;
import co.nstant.in.cbor.model.UnsignedInteger;

public class CardanoUtxo extends RegistryItem {
    private static final int TRANSACTION_HASH = 1;
    private static final int INDEX = 2;
    private static final int VALUE = 3;
    private static final int PATH = 4;
    private static final int ADDRESS = 5;

    private final byte[] transactionHash;
    private final int index;
    private final String value;
    private final CryptoKeypath path;
    private final String address;

    public CardanoUtxo(byte[] transactionHash, int index, String value, CryptoKeypath path, String address) {
        this.transactionHash = transactionHash;
        this.index = index;
        this.value = value;
        this.path = path;
        this.address = address;
    }

    public byte[] getTransactionHash() {
        return transactionHash;
    }

    public int getIndex() {
        return index;
    }

    public String getValue() {
        return value;
    }

    public CryptoKeypath getPath() {
        return path;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public DataItem toCbor() {
        Map map = new Map();
        map.put(new UnsignedInteger(TRANSACTION_HASH), new ByteString(transactionHash));
        map.put(new UnsignedInteger(INDEX), new UnsignedInteger(index));
        map.put(new UnsignedInteger(VALUE), new UnicodeString(value));
        DataItem keypath = path.toCbor();
        keypath.setTag(path.getRegistryType().getTag());
        map.put(new UnsignedInteger(PATH), keypath);
        map.put(new UnsignedInteger(ADDRESS), new UnicodeString(address));
        return map;
    }

    public static CardanoUtxo fromCbor(DataItem item) {
        byte[] transactionHash = null;
        int index = 0;
        String value = "0";
        CryptoKeypath path = null;
        String address = null;
        Map map = (Map) item;

        for (DataItem key : map.getKeys()) {
            UnsignedInteger uintKey = (UnsignedInteger) key;
            int intKey = uintKey.getValue().intValue();
            if (intKey == TRANSACTION_HASH) {
                transactionHash = ((ByteString) map.get(uintKey)).getBytes();
            }
            if (intKey == INDEX) {
                index = ((UnsignedInteger) map.get(uintKey)).getValue().intValue();
            }
            if (intKey == VALUE) {
                value = ((UnicodeString) map.get(uintKey)).getString();
            }
            if (intKey == PATH) {
                path = CryptoKeypath.fromCbor(map.get(uintKey));
            }
            if (intKey == ADDRESS) {
                address = ((UnicodeString) map.get(uintKey)).getString();
            }
        }

        return new CardanoUtxo(transactionHash, index, value, path, address);
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.CARDANO_UTXO;
    }
}
