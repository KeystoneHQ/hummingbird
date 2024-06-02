package com.sparrowwallet.hummingbird.registry.cardano;

import com.sparrowwallet.hummingbird.registry.RegistryItem;
import com.sparrowwallet.hummingbird.registry.RegistryType;

import java.util.ArrayList;
import java.util.List;

import co.nstant.in.cbor.model.Array;
import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnicodeString;
import co.nstant.in.cbor.model.UnsignedInteger;

public class CardanoSignRequest extends RegistryItem {
    private static final int REQUEST_ID = 1;
    private static final int SIGN_DATA = 2;
    private static final int UTXOS = 3;
    private static final int EXTRA_SIGNERS = 4;
    private static final int ORIGIN = 5;

    private final byte[] requestId;
    private final byte[] signData;
    private final List<CardanoUtxo> utxos;
    private final List<CardanoCertKey> extraSigners;
    private final String origin;

    public CardanoSignRequest(byte[] requestId, byte[] signData, List<CardanoUtxo> utxos, List<CardanoCertKey> cardanoCertKeys, String origin) {
        this.requestId = requestId;
        this.signData = signData;
        this.utxos = utxos;
        this.extraSigners = cardanoCertKeys;
        this.origin = origin;
    }

    public byte[] getRequestId() {
        return requestId;
    }

    public byte[] getSignData() {
        return signData;
    }

    public List<CardanoUtxo> getUtxos() {
        return utxos;
    }

    public List<CardanoCertKey> getExtraSigners() {
        return extraSigners;
    }

    public String getOrigin() {
        return origin;
    }

    @Override
    public DataItem toCbor() {
        Map map = new Map();
        if (requestId != null) {
            DataItem id = new ByteString(requestId);
            id.setTag(37);
            map.put(new UnsignedInteger(REQUEST_ID), id);
        }
        map.put(new UnsignedInteger(SIGN_DATA), new ByteString(signData));
        Array array = new Array();
        for (CardanoUtxo utxo : utxos) {
            DataItem u = utxo.toCbor();
            u.setTag(utxo.getRegistryType().getTag());
            array.add(u);
        }
        map.put(new UnsignedInteger(UTXOS), array);
        Array keys = new Array();
        for (CardanoCertKey cardanoCertKey : extraSigners) {
            DataItem c = cardanoCertKey.toCbor();
            c.setTag(cardanoCertKey.getRegistryType().getTag());
            keys.add(c);
        }
        map.put(new UnsignedInteger(EXTRA_SIGNERS), keys);
        if (origin != null) {
            map.put(new UnsignedInteger(ORIGIN), new UnicodeString(origin));
        }
        return map;
    }

    public static CardanoSignRequest fromCbor(DataItem item) {
        byte[] requestId = null;
        byte[] signData = null;
        List<CardanoUtxo> utxos = new ArrayList<>();
        List<CardanoCertKey> certKeys = new ArrayList<>();
        String origin = null;

        Map map = (Map) item;

        for (DataItem key : map.getKeys()) {
            UnsignedInteger uintKey = (UnsignedInteger) key;
            int intKey = uintKey.getValue().intValue();
            if (intKey == REQUEST_ID) {
                requestId = ((ByteString) map.get(uintKey)).getBytes();
            }
            if (intKey == SIGN_DATA) {
                signData = ((ByteString) map.get(uintKey)).getBytes();
            }
            if (intKey == UTXOS) {
                List<DataItem> array = ((Array) map.get(uintKey)).getDataItems();
                for (DataItem dataItem : array) {
                    utxos.add(CardanoUtxo.fromCbor(dataItem));
                }
            }
            if (intKey == EXTRA_SIGNERS) {
                List<DataItem> array = ((Array) map.get(uintKey)).getDataItems();
                for (DataItem dataItem : array) {
                    certKeys.add(CardanoCertKey.fromCbor(dataItem));
                }
            }
            if (intKey == ORIGIN) {
                origin = ((UnicodeString) map.get(uintKey)).getString();
            }
        }

        return new CardanoSignRequest(requestId, signData, utxos, certKeys, origin);
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.CARDANO_SIGN_REQUEST;
    }
}
