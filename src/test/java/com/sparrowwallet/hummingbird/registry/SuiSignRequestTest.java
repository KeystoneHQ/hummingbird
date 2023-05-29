package com.sparrowwallet.hummingbird.registry;

import com.sparrowwallet.hummingbird.TestUtils;
import com.sparrowwallet.hummingbird.UR;
import com.sparrowwallet.hummingbird.URDecoder;
import com.sparrowwallet.hummingbird.registry.sui.SuiSignRequest;

import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class SuiSignRequestTest {

    @Test
    public void testSuiSignRequest() throws UR.URException {
        String signHex = "00000200201ff915a5e9e32fdbe0135535b6c69a00a9809aaf7f7c0275d3239ca79db20d6400081027000000000000020200010101000101020000010000ebe623e33b7307f1350f8934beb3fb16baef0fc1b3f1b92868eec3944093886901a2e3e42930675d9571a467eb5d4b22553c93ccb84e9097972e02c490b4e7a22ab73200000000000020176c4727433105da34209f04ac3f22e192a2573d7948cb2fabde7d13a7f4f149ebe623e33b7307f1350f8934beb3fb16baef0fc1b3f1b92868eec39440938869e803000000000000640000000000000000";
        byte[] signData = TestUtils.hexToBytes(signHex);

        UUID uuid = UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d");
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        byte[] uuidBytes = bb.array();


        List<PathComponent> components = new ArrayList<>();
        components.add(new PathComponent(44, true));
        components.add(new PathComponent(784, true));
        components.add(new PathComponent(0, true));
        components.add(new PathComponent(0, true));
        components.add(new PathComponent(0, true));


        String masterFinger = "78230804";
        CryptoKeypath keyPath = new CryptoKeypath(components, TestUtils.hexToBytes(masterFinger), null);


        SuiSignRequest suiSignRequest = new SuiSignRequest(signData, 1, Arrays.asList(keyPath), uuidBytes,
                Arrays.asList(TestUtils.hexToBytes("ebe623e33b7307f1350f8934beb3fb16baef0fc1b3f1b92868eec39440938869")), "sui wallet");


        String urStr = "ur:sui-sign-request/oladtpdagdndcawmgtfrkigrpmndutdnbtkgfssbjnaohdtaaeaeaoaecxctytbzonwlvldluyvtbwgoecrpswnyaeptlanypelbkeaokptecnnsosntprbtieaeaybediaeaeaeaeaeaeaoaoaeadadadaeadadaoaeaeadaeaewmvacnvlfrjkatwnecbsldeernqdzocmrdwsbsseqdwnrhdeiswysrmwfzmuloinadoevlvedtdyiohlmdjsoxiowmhlgrcpgofnmusfroglmhmsmsdmaossmhqzvdoedrrleyaeaeaeaeaeaecxchjzfldifxehahtneecxneaapsfhcpvymooehgfskkfdsbdlpyuekibwoswkwngawmvacnvlfrjkatwnecbsldeernqdzocmrdwsbsseqdwnrhdeiswysrmwfzmuloinvsaxaeaeaeaeaeaeieaeaeaeaeaeaeaeaeaxadaalytaaddyoeadlecsdwykcfaxbeykaeykaeykaeykaocykscnayaaahlyhdcxwmvacnvlfrjkatwnecbsldeernqdzocmrdwsbsseqdwnrhdeiswysrmwfzmuloinamimjkkpincxkthsjzjzihjylalejzpm";
        Assert.assertEquals(urStr, suiSignRequest.toUR().toString());

        UR ur = URDecoder.decode(urStr);
        SuiSignRequest suiSignRequest1 = (SuiSignRequest) ur.decodeFromRegistry();
        Assert.assertArrayEquals(signData, suiSignRequest1.getSignData());
    }
}
