package com.sparrowwallet.hummingbird.registry;

import co.nstant.in.cbor.model.*;


public class KeystoneSignRequest extends RegistryItem {
    public static final int SIGN_DATA = 1;
    public static final int ORIGIN = 2;


    private final byte[] signData;
    private final String origin;


    public KeystoneSignRequest(byte[] signData,String origin) {
        this.signData = signData;
        this.origin = origin;
    }
    
    public String getOrigin() {
        return origin;
    }

    public byte[] getSignData() {
        return signData;
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.KEYSTONE_SIGN_REQUEST;
    }

    @Override
    public DataItem toCbor() {
        Map map = new Map();
        map.put(new UnsignedInteger(SIGN_DATA), new ByteString(signData));
        if (origin != null) {
            map.put(new UnsignedInteger(ORIGIN), new UnicodeString(origin));
        }
        return map;
    }

    public static KeystoneSignRequest fromCbor(DataItem item) {
        byte[] signData = null;
        String origin = null;

        Map map = (Map)item;
        for(DataItem key : map.getKeys()) {
            UnsignedInteger uintKey = (UnsignedInteger) key;
            int intKey = uintKey.getValue().intValue();
            if (intKey == SIGN_DATA) {
                signData = ((ByteString) map.get(uintKey)).getBytes();
            }else if (intKey == ORIGIN) {
                origin = ((UnicodeString) map.get(uintKey)).getString();
            }
        }
        if(signData == null) {
            throw new IllegalStateException("required data field is missing");
        }

        return new KeystoneSignRequest(signData, origin);
    }

    
}
