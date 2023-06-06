package com.sparrowwallet.hummingbird.registry.extend;

import com.sparrowwallet.hummingbird.TestUtils;
import com.sparrowwallet.hummingbird.registry.CryptoKeypath;
import com.sparrowwallet.hummingbird.registry.PathComponent;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import co.nstant.in.cbor.CborDecoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.DataItem;

public class QRHardwareCallTest {

    @Test
    public void testEncode() throws CborException {
        List<PathComponent> components = new ArrayList<>();
        components.add(new PathComponent(44, true));
        components.add(new PathComponent(0, true));
        components.add(new PathComponent(0, true));
        CryptoKeypath keyPath = new CryptoKeypath(components, null, null);
        KeyDerivationSchema schema1 = new KeyDerivationSchema(
                keyPath
        );

        List<PathComponent> components2 = new ArrayList<>();
        components2.add(new PathComponent(44, true));
        components2.add(new PathComponent(501, true));
        components2.add(new PathComponent(0, true));
        components2.add(new PathComponent(0, true));
        components2.add(new PathComponent(0, false));
        CryptoKeypath keyPath2 = new CryptoKeypath(components2, null, null);
        KeyDerivationSchema schema2 = new KeyDerivationSchema(
                keyPath2,
                KeyDerivationSchema.Curve.Ed25519
        );

        List<KeyDerivationSchema> schemas = new ArrayList<>();
        schemas.add(schema1);
        schemas.add(schema2);

        KeyDerivationCall keyDerivationCall = new KeyDerivationCall(schemas);

        QRHardwareCall call = new QRHardwareCall(keyDerivationCall);
        String cbor = TestUtils.encode(call.toCbor());
        Assert.assertEquals("a2010002d90515a10182d90516a101d90130a10186182cf500f500f5d90516a201d90130a1018a182cf51901f5f500f500f500f40201", cbor);
    }

    @Test
    public void testDecode() throws CborException {
        List<DataItem> dataItems = CborDecoder.decode(TestUtils.hexToBytes("a2010002d90515a10182d90516a101d90130a10186182cf500f500f5d90516a201d90130a1018a182cf51901f5f500f500f500f40201"));
        QRHardwareCall call = QRHardwareCall.fromCbor(dataItems.get(0));
        Assert.assertEquals(CallParams.CallType.KeyDerivation.ordinal(), call.getCallType().ordinal());
        KeyDerivationCall keyDerivationCall = (KeyDerivationCall) call.getCallParams();
        Assert.assertEquals(2, keyDerivationCall.getSchemas().size());
        KeyDerivationSchema schema1 = keyDerivationCall.getSchemas().get(0);
        KeyDerivationSchema schema2 = keyDerivationCall.getSchemas().get(1);
        CryptoKeypath keypath =  schema1.getKeypath();
        Assert.assertEquals("44'/0'/0'", keypath.getPath());
        Assert.assertEquals(KeyDerivationSchema.Algo.Slip10, schema1.getAlgo());
        Assert.assertEquals(KeyDerivationSchema.Curve.Secp256k1, schema1.getCurve());
        CryptoKeypath keypath2 =  schema2.getKeypath();
        Assert.assertEquals("44'/501'/0'/0'/0", keypath2.getPath());
        Assert.assertEquals(KeyDerivationSchema.Algo.Slip10, schema2.getAlgo());
        Assert.assertEquals(KeyDerivationSchema.Curve.Ed25519, schema2.getCurve());
    }
}