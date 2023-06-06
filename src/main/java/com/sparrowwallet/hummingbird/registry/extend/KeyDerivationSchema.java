package com.sparrowwallet.hummingbird.registry.extend;

import com.sparrowwallet.hummingbird.registry.CryptoKeypath;
import com.sparrowwallet.hummingbird.registry.RegistryItem;
import com.sparrowwallet.hummingbird.registry.RegistryType;

import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnsignedInteger;

public class KeyDerivationSchema extends RegistryItem {
    private static final int KEY_PATH = 1;
    private static final int CURVE = 2;
    private static final int ALGO = 3;

    private final CryptoKeypath keypath;
    private final Curve curve;
    private final Algo algo;

    public KeyDerivationSchema(CryptoKeypath keypath, Curve curve, Algo algo) {
        this.keypath = keypath;
        this.curve = curve;
        this.algo = algo;
    }

    public KeyDerivationSchema(CryptoKeypath keypath, Curve curve) {
        this.keypath = keypath;
        this.curve = curve;
        this.algo = null;
    }

    public KeyDerivationSchema(CryptoKeypath keypath) {
        this.keypath = keypath;
        this.curve = null;
        this.algo = null;
    }

    public CryptoKeypath getKeypath() {
        return keypath;
    }

    public Curve getCurve() {
        return curve == null ? Curve.Secp256k1 : curve;
    }

    public Algo getAlgo() {
        return algo == null ? Algo.Slip10 : algo;
    }

    @Override
    public DataItem toCbor() {
        Map map = new Map();
        DataItem path = keypath.toCbor();
        path.setTag(keypath.getRegistryType().getTag());
        map.put(new UnsignedInteger(KEY_PATH), path);
        if (curve != null) {
            map.put(new UnsignedInteger(CURVE), new UnsignedInteger(curve.ordinal()));
        }
        if (algo != null) {
            map.put(new UnsignedInteger(ALGO), new UnsignedInteger(algo.ordinal()));
        }
        return map;
    }

    public static KeyDerivationSchema fromCbor(DataItem item) {
        CryptoKeypath keypath = null;
        Curve curve = null;
        Algo algo = null;

        Map map = (Map) item;
        for (DataItem key : map.getKeys()) {
            UnsignedInteger uintKey = (UnsignedInteger) key;
            int intKey = uintKey.getValue().intValue();
            switch (intKey) {
                case KEY_PATH: {
                    keypath = CryptoKeypath.fromCbor(map.get(uintKey));
                    break;
                }
                case CURVE: {
                    curve = Curve.fromOrdinal(((UnsignedInteger) map.get(uintKey)).getValue().intValue());
                    break;
                }
                case ALGO: {
                    algo = Algo.fromOrdinal(((UnsignedInteger) map.get(uintKey)).getValue().intValue());
                    break;
                }
            }
        }

        if (keypath == null) {
            throw new IllegalArgumentException("key derivation schema requires a key path");
        }

        return new KeyDerivationSchema(keypath, curve, algo);
    }

    @Override
    public RegistryType getRegistryType() {
        return RegistryType.KEY_DERIVATION_SCHEMA;
    }

    public enum Curve {
        Secp256k1,
        Ed25519;

        public static Curve fromOrdinal(int value) {
            switch (value) {
                case 0: {
                    return Secp256k1;
                }
                case 1: {
                    return Ed25519;
                }
            }
            throw new IllegalArgumentException("invalid curve type: " + value);
        }
    }

    public enum Algo {
        Slip10,
        Bip32Ed25519;

        public static Algo fromOrdinal(int value) {
            switch (value) {
                case 0: {
                    return Slip10;
                }
                case 1: {
                    return Bip32Ed25519;
                }
            }
            throw new IllegalArgumentException("invalid algo type: " + value);
        }
    }

}
