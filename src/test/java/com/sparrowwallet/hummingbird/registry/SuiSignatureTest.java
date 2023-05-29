package com.sparrowwallet.hummingbird.registry;

import com.sparrowwallet.hummingbird.TestUtils;
import com.sparrowwallet.hummingbird.UR;
import com.sparrowwallet.hummingbird.URDecoder;
import com.sparrowwallet.hummingbird.registry.sui.SuiSignature;

import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.UUID;

public class SuiSignatureTest {

    @Test
    public void testSuiSignature() throws UR.URException {
        UUID uuid = UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d");
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        byte[] uuidBytes = bb.array();

        byte[] signature = TestUtils.hexToBytes("f4b79835417490958c72492723409289b444f3af18274ba484a9eeaca9e760520e453776e5975df058b537476932a45239685f694fc6362fe5af6ba714da6505");

        byte[] publicKey = TestUtils.hexToBytes("aeb28ecace5c664c080e71b9efd3d071b3dac119a26f4e830dd6bd06712ed93f");

        SuiSignature suiSignature = new SuiSignature(signature, uuidBytes, publicKey);

        String urString = "ur:sui-signature/otadtpdagdndcawmgtfrkigrpmndutdnbtkgfssbjnaohdfzwkrlmkecfpjymhmdlkjpgadicnfzmoldqzfywfpecsdigroxlrptwypsptvdhngmbafeemkovwmshlwthdreemflineyoxgmesisheingwswendlvwpejeosbbtnihahaxhdcxplprmnsgtohhiygsaybajsrhwstetijsqdtnsecfoejlgllsbttbryamjsdmtafhdwqzoehf";

        Assert.assertEquals(urString, suiSignature.toUR().toString());

        UR ur = URDecoder.decode(urString);
        SuiSignature decodedSuiSignature = (SuiSignature) ur.decodeFromRegistry();

        Assert.assertArrayEquals(signature, decodedSuiSignature.getSignature());
    }
}
