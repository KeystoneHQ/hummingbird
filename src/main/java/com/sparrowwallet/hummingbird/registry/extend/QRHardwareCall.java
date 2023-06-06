package com.sparrowwallet.hummingbird.registry.extend;

import com.sparrowwallet.hummingbird.registry.RegistryItem;
import com.sparrowwallet.hummingbird.registry.RegistryType;

import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnicodeString;
import co.nstant.in.cbor.model.UnsignedInteger;

public class QRHardwareCall extends RegistryItem {
    private static final int CALL_TYPE = 1;
    private static final int PARAMS = 2;
    private static final int ORIGIN = 3;

    private final CallParams.CallType callType;
    private final CallParams callParams;
    private final String origin;

    public CallParams.CallType getCallType() {
        return callType;
    }

    public CallParams getCallParams() {
        return callParams;
    }

    public String getOrigin() {
        return origin;
    }

    public QRHardwareCall(CallParams callParams) {
        this.callType = callParams.getCallType();
        this.callParams = callParams;
        this.origin = null;
    }

    public QRHardwareCall(CallParams callParams, String origin) {
        this.callType = callParams.getCallType();
        this.callParams = callParams;
        this.origin = origin;
    }

    @Override
    public DataItem toCbor() {
        Map map = new Map();
        map.put(new UnsignedInteger(CALL_TYPE), new UnsignedInteger(callType.ordinal()));
        DataItem params = callParams.toCbor();
        params.setTag(callParams.getRegistryType().getTag());
        map.put(new UnsignedInteger(PARAMS), params);
        if (origin != null) {
            map.put(new UnsignedInteger(ORIGIN), new UnicodeString(origin));
        }
        return map;
    }

    public static QRHardwareCall fromCbor(DataItem item) {
        CallParams.CallType callType = CallParams.CallType.KeyDerivation;
        CallParams callParams = null;
        String origin = null;
        Map map = (Map) item;
        for (DataItem key : map.getKeys()) {
            UnsignedInteger uintKey = (UnsignedInteger) key;
            int intKey = uintKey.getValue().intValue();
            switch (intKey) {
                case CALL_TYPE: {
                    callType = CallParams.CallType.fromOrdinal(((UnsignedInteger) map.get(uintKey)).getValue().intValue());
                    break;
                }
                case PARAMS: {
                    DataItem params = map.get(uintKey);
                    switch (callType) {
                        case KeyDerivation: {
                            callParams = KeyDerivationCall.fromCbor(params);
                        }
                    }
                    break;
                }
                case ORIGIN: {
                    origin = ((UnicodeString) map.get(uintKey)).getString();
                }
            }
        }
        if (callParams == null) {
            throw new IllegalArgumentException("invalid call params");
        }
        return new QRHardwareCall(callParams, origin);
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.QR_HARDWARE_CALL;
    }
}
