package com.sparrowwallet.hummingbird.registry;

import com.sparrowwallet.hummingbird.TestUtils;
import com.sparrowwallet.hummingbird.UR;
import com.sparrowwallet.hummingbird.URDecoder;
import com.sparrowwallet.hummingbird.registry.cosmos.CosmosSignRequest;

import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CosmosSignRequestTest {

    @Test
    public void testCosmosSignRequest() throws UR.URException {
        String signHex = "8e53e7b10656816de70824e3016fc1a277e77825e12825dc4f239f418ab2e04e";
        byte[] signData = TestUtils.hexToBytes(signHex);

        UUID uuid = UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d");
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        byte[] uuidBytes = bb.array();


        List<PathComponent> components1 = new ArrayList<>();
        components1.add(new PathComponent(44, true));
        components1.add(new PathComponent(118, true));
        components1.add(new PathComponent(0, true));
        components1.add(new PathComponent(0, false));
        components1.add(new PathComponent(0, false));

        List<PathComponent> components2 = new ArrayList<>();
        components2.add(new PathComponent(44, true));
        components2.add(new PathComponent(529, true));
        components2.add(new PathComponent(0, true));
        components2.add(new PathComponent(0, false));
        components2.add(new PathComponent(0, false));


        String masterFinger1 = "78230804";
        String masterFinger2 = "78230805";
        CryptoKeypath keyPath1 = new CryptoKeypath(components1, TestUtils.hexToBytes(masterFinger1), null);
        CryptoKeypath keyPath2 = new CryptoKeypath(components2, TestUtils.hexToBytes(masterFinger2), null);


        CosmosSignRequest cosmosSignRequest = new CosmosSignRequest(signData, Arrays.asList(keyPath1,keyPath2), uuidBytes,
                Arrays.asList("cosmos13nmjt4hru5ag0c6q3msk0srs55qd3dtme8wgep", "secret1e63tmjmhsac8ja86gk8kh06uqxh2h9hwkcs5cs"), "cosmos wallet", 1);


        String urStr = "ur:cosmos-sign-request/oladtpdagdndcawmgtfrkigrpmndutdnbtkgfssbjnaohdcxmnguvdpaamhflyjnvdaydkvladjlseoektvdksdavydedauogwcnnefpleprvtglaxadaalftaaddyoeadlecsdwykcskoykaeykaewkaewkaocykscnayaataaddyoeadlecsdwykcfaobyykaeykaewkaewkaocykscnayahahlfksdpiajljkjnjljkeheojtjnimjyeeisjpkpechsiodyiaenjseojnjkjedyjkjpjkececjsieeoiejyjnihetktioihjoksdpjkihiajpihjyehiheneojyjnimjnisjkhsiaetimhseteniojeetjeisdyenkpjsksiseyisesisktjeiajkeciajkamjniajljkjnjljkcxkthsjzjzihjytylgwfat";
        Assert.assertEquals(urStr, cosmosSignRequest.toUR().toString());

        UR ur = URDecoder.decode(urStr);
        CosmosSignRequest cosmosSignRequest1 = (CosmosSignRequest) ur.decodeFromRegistry();
        Assert.assertArrayEquals(signData, cosmosSignRequest1.getSignData());
    }
}
