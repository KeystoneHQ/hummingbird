package com.sparrowwallet.hummingbird.registry;
import co.nstant.in.cbor.CborDecoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.DataItem;
import com.sparrowwallet.hummingbird.TestUtils;
import org.junit.Assert;
import org.junit.Test;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.*;
import java.util.List;

public class KeystoneSignRequestTest {
    @Test 
    public void testKeystoneSignRequest() throws CborException {    
        String hex = "a2015901581f8b08000000000000ff554d3f4b23411c256bb36c93d52aa40a8b10092c9999dfecfc812beee21214d6603410926e7e33b345305993dcc5fb187e04bf805c7f1f4041b03bacafbd43eceddc56783c788ff7270c0e9ae3cd71e57ce77c537daf6c75d57e096a371c3218ea61ce92c720da2b26c707871aa9f3c85d0a285dcad1b854a3732943e22482b3285ce7d7dbdfdfefe428c4db207cda8ffff492bb46f4d5641a2d1a633ca5580b501cb867ae66c538f8cc8366594669262c37e095f2129d25c88c2a8580b8d13e8d0684694189408568b4954a122141d61525bc744a010a2d35ad170918414de93595256619f1e8508385d6c3fdff30692efb9c77fbacdb2735faa4d78d9262b6292a9c9f70ab2793ed6a9da3dbde4caf776b3d6363287ecc647c3bea7da983733d5aad0aff53bb3cf7ebc1d6dfecd8e0b254cb6ab1b8f866a610bf3eff0b5b8da479f6f9e603ce1eec266c01000002696c746357616c6c6574";
        byte[] cborPayload = TestUtils.hexToBytes(hex);
        List<DataItem> items = CborDecoder.decode(cborPayload);
        KeystoneSignRequest originRequest = KeystoneSignRequest.fromCbor(items.get(0));
        String hex_sign_data = TestUtils.bytesToHex(originRequest.getSignData());
        String origin = originRequest.getOrigin();
        Assert.assertEquals("1f8b08000000000000ff554d3f4b23411c256bb36c93d52aa40a8b10092c9999dfecfc812beee21214d6603410926e7e33b345305993dcc5fb187e04bf805c7f1f4041b03bacafbd43eceddc56783c788ff7270c0e9ae3cd71e57ce77c537daf6c75d57e096a371c3218ea61ce92c720da2b26c707871aa9f3c85d0a285dcad1b854a3732943e22482b3285ce7d7dbdfdfefe428c4db207cda8ffff492bb46f4d5641a2d1a633ca5580b501cb867ae66c538f8cc8366594669262c37e095f2129d25c88c2a8580b8d13e8d0684694189408568b4954a122141d61525bc744a010a2d35ad170918414de93595256619f1e8508385d6c3fdff30692efb9c77fbacdb2735faa4d78d9262b6292a9c9f70ab2793ed6a9da3dbde4caf776b3d6363287ecc647c3bea7da983733d5aad0aff53bb3cf7ebc1d6dfecd8e0b254cb6ab1b8f866a610bf3eff0b5b8da479f6f9e603ce1eec266c010000",
            hex_sign_data);
        Assert.assertEquals("ltcWallet", origin);
    }

    @Test 
    public void testKeystoneSignRequestToCbor() throws CborException {
        String hex_sign_data = "1f8b08000000000000ff554d3f4b23411c256bb36c93d52aa40a8b10092c9999dfecfc812beee21214d6603410926e7e33b345305993dcc5fb187e04bf805c7f1f4041b03bacafbd43eceddc56783c788ff7270c0e9ae3cd71e57ce77c537daf6c75d57e096a371c3218ea61ce92c720da2b26c707871aa9f3c85d0a285dcad1b854a3732943e22482b3285ce7d7dbdfdfefe428c4db207cda8ffff492bb46f4d5641a2d1a633ca5580b501cb867ae66c538f8cc8366594669262c37e095f2129d25c88c2a8580b8d13e8d0684694189408568b4954a122141d61525bc744a010a2d35ad170918414de93595256619f1e8508385d6c3fdff30692efb9c77fbacdb2735faa4d78d9262b6292a9c9f70ab2793ed6a9da3dbde4caf776b3d6363287ecc647c3bea7da983733d5aad0aff53bb3cf7ebc1d6dfecd8e0b254cb6ab1b8f866a610bf3eff0b5b8da479f6f9e603ce1eec266c010000";
        String origin = "ltcWallet";
        byte[] signData = TestUtils.hexToBytes(hex_sign_data);
        KeystoneSignRequest keystoneSignRequest = new KeystoneSignRequest(signData, origin);
        DataItem cbor_data = keystoneSignRequest.toCbor();
        Map map = (Map) cbor_data;
        for (DataItem key : map.getKeys()) {
            UnsignedInteger uintKey = (UnsignedInteger) key;
            int intKey = uintKey.getValue().intValue();
            if (intKey == 1) {
                byte[] signData_t = ((ByteString) map.get(uintKey)).getBytes();
                Assert.assertEquals("1f8b08000000000000ff554d3f4b23411c256bb36c93d52aa40a8b10092c9999dfecfc812beee21214d6603410926e7e33b345305993dcc5fb187e04bf805c7f1f4041b03bacafbd43eceddc56783c788ff7270c0e9ae3cd71e57ce77c537daf6c75d57e096a371c3218ea61ce92c720da2b26c707871aa9f3c85d0a285dcad1b854a3732943e22482b3285ce7d7dbdfdfefe428c4db207cda8ffff492bb46f4d5641a2d1a633ca5580b501cb867ae66c538f8cc8366594669262c37e095f2129d25c88c2a8580b8d13e8d0684694189408568b4954a122141d61525bc744a010a2d35ad170918414de93595256619f1e8508385d6c3fdff30692efb9c77fbacdb2735faa4d78d9262b6292a9c9f70ab2793ed6a9da3dbde4caf776b3d6363287ecc647c3bea7da983733d5aad0aff53bb3cf7ebc1d6dfecd8e0b254cb6ab1b8f866a610bf3eff0b5b8da479f6f9e603ce1eec266c010000", TestUtils.bytesToHex(signData_t));
            } else if (intKey == 2) {
                String origin_t = ((UnicodeString) map.get(uintKey)).getString();
                Assert.assertEquals("ltcWallet", origin_t);
            }
        }
    }
}