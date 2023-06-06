package com.sparrowwallet.hummingbird.registry.extend;

import com.sparrowwallet.hummingbird.registry.RegistryType;

import java.util.ArrayList;
import java.util.List;

import co.nstant.in.cbor.model.Array;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnsignedInteger;

public class KeyDerivationCall extends CallParams {
    private final static int SCHEMAS = 1;

    private final List<KeyDerivationSchema> schemas;

    public KeyDerivationCall(List<KeyDerivationSchema> schemas) {
        this.schemas = schemas;
    }

    public List<KeyDerivationSchema> getSchemas() {
        return schemas;
    }

    @Override
    public DataItem toCbor() {
        Map map = new Map();
        Array array = new Array();
        for (KeyDerivationSchema schema : schemas) {
            DataItem dataItem = schema.toCbor();
            dataItem.setTag(schema.getRegistryType().getTag());
            array.add(dataItem);
        }
        map.put(new UnsignedInteger(SCHEMAS), array);
        return map;
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.KEY_DERIVATION_CALL;
    }

    @Override
    public CallType getCallType() {
        return CallType.KeyDerivation;
    }

    public static KeyDerivationCall fromCbor(DataItem item) {
        List<KeyDerivationSchema> schemas = new ArrayList<>();
        Map map = (Map) item;
        for (DataItem key : map.getKeys()) {
            UnsignedInteger uintKey = (UnsignedInteger) key;
            int intKey = uintKey.getValue().intValue();
            if (intKey == SCHEMAS) {
                Array array = (Array) map.get(uintKey);
                for (DataItem dataItem : array.getDataItems()) {
                    schemas.add(KeyDerivationSchema.fromCbor(dataItem));
                }
            }
        }
        return new KeyDerivationCall(schemas);
    }
}
