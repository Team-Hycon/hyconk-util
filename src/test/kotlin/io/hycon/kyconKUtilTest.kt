package io.hycon

import org.bouncycastle.util.encoders.DecoderException
import org.junit.Assert.assertEquals
import org.junit.Test
import java.security.NoSuchAlgorithmException
import javax.security.auth.login.AccountException



class HyconKUtilTest {
    var utils = HyconkUtil()
    @Test
    @Throws(NoSuchAlgorithmException::class, DecoderException::class)
    fun blake2bByStringTest() {
        val str = "02c4199d83e47650b854e027188eade5378d19c94c13b226f43310fb144bc224af"
        val result = utils.blake2bHash(str)

        assertEquals(
            "dafec57d0062e2317f6d0f294366e2a531a891233fd59cfa5f062a0f1018af6a",
            utils.encodeHexByteArrayToString(result)
        )
    }

    @Test
    @Throws(NoSuchAlgorithmException::class, DecoderException::class)
    fun blake2bByByteTest() {
        val str = "02c4199d83e47650b854e027188eade5378d19c94c13b226f43310fb144bc224af"
        val input = utils.decodeHexStringToByteArray(str)
        val result = utils.blake2bHash(input)

        assertEquals(
            "dafec57d0062e2317f6d0f294366e2a531a891233fd59cfa5f062a0f1018af6a",
            utils.encodeHexByteArrayToString(result)
        )
    }

    @Test
    @Throws(DecoderException::class)
    fun base58EncodeTest() {
        val input = utils.decodeHexStringToByteArray("4366e2a531a891233fd59cfa5f062a0f1018af6a")
        val result = utils.base58Encode(input)

        assertEquals("wTsQGpbicAZsXcmSHN8XmcNR9wX", result)
    }

    @Test
    @Throws(DecoderException::class, AccountException::class)
    fun base58DecodeTest() {
        val result = utils.base58Decode("wTsQGpbicAZsXcmSHN8XmcNR9wX")

        assertEquals("4366e2a531a891233fd59cfa5f062a0f1018af6a", utils.encodeHexByteArrayToString(result))
    }

    @Test
    @Throws(DecoderException::class, NoSuchAlgorithmException::class)
    fun publicKeyToAddressTest() {
        val str = "02c4199d83e47650b854e027188eade5378d19c94c13b226f43310fb144bc224af"
        val input = utils.decodeHexStringToByteArray(str)
        val result = utils.publicKeyToAddress(input)

        assertEquals("4366e2a531a891233fd59cfa5f062a0f1018af6a", utils.encodeHexByteArrayToString(result))
    }

    @Test
    @Throws(DecoderException::class, NoSuchAlgorithmException::class)
    fun addresssCheckSumTest() {
        val str = "4366e2a531a891233fd59cfa5f062a0f1018af6a"
        val input = utils.decodeHexStringToByteArray(str)
        val result = utils.addresssCheckSum(input)

        assertEquals("Htw7", result)
    }

    @Test
    @Throws(DecoderException::class, NoSuchAlgorithmException::class)
    fun addressToStringTest() {
        val str = "4366e2a531a891233fd59cfa5f062a0f1018af6a"
        val input = utils.decodeHexStringToByteArray(str)
        val result = utils.addressToString(input)

        assertEquals("HwTsQGpbicAZsXcmSHN8XmcNR9wXHtw7", result)
    }

    @Test
    @Throws(Exception::class)
    fun addressToByteArrayTest() {
        val result = utils.addressToByteArray("HwTsQGpbicAZsXcmSHN8XmcNR9wXHtw7")

        assertEquals("4366e2a531a891233fd59cfa5f062a0f1018af6a", utils.encodeHexByteArrayToString(result))
    }

    @Test
    fun hyconToFromStringTest() {
        val a = utils.hyconFromString("10.000000001")
        val b = utils.hyconFromString("7.123456789")

        val result = a + b

        assertEquals("17.12345679", utils.hyconToString(result))
    }

    @Test
    @Throws(Exception::class)
    fun signTxTest() {
        val strArr = utils.signTx(
            "H3N2sCstx81NvvVy3hkrhGsNS43834YWw",
            "H497fHm8gbPZxaXySKpV17a7beYBF9Ut3",
            "0.000000001",
            "0.000000001",
            1024,
            "e09167abb9327bb3748e5dd1b9d3d40832b33eb0b041deeee8e44ff47030a61d",
            "hycon"
        )

        assertEquals(
            "fd67de0827ccf8bc957eeb185ba0ea78aa1cd5cad74aea40244361ee7df68e36025aebc4ae6b18628135ea3ef5a70ea3681a7082c44af0899f0f59b50f2707b9",
            strArr[0]
        )
        assertEquals("1", strArr[1])
    }

    @Test
    @Throws(NoSuchAlgorithmException::class)
    fun createWalletTest() {
        val result =
            utils.createWallet("ring crime symptom enough erupt lady behave ramp apart settle citizen junk", "")

        assertEquals("HwTsQGpbicAZsXcmSHN8XmcNR9wXHtw7", result[0])
        assertEquals("f35776c86f811d9ab1c66cadc0f503f519bf21898e589c2f26d646e472bfacb2", result[1])
    }

    @Test
    @Throws(NoSuchAlgorithmException::class)
    fun createWalletWithPassPhraseTest() {
        val result = utils.createWallet(
            "way prefer push tooth bench hover orchard brother crumble nothing wink retire",
            "TREZOR"
        )

        assertEquals("H3fFn71jR6G33sAVMASDtLFhrq38h8FQ1", result[0])
        assertEquals("4c28ef543da7ee616d91ba786ce73ef02cf29818f3cdf5a4639771921a2cf843", result[1])
    }

    @Test
    @Throws(Exception::class)
    fun createHDWalletTest() {
        val result =
            utils.createHDWallet("length segment syrup visa lava beach rain crush false reveal alone olympic", "")

        assertEquals(
            "xprv9s21ZrQH143K2gffZBzfnUUUjR5MfiQKNj1xXfwuHtxu7yzAPTMC6Gr6D5Krx2nPWVHoe6xDFTV6h6A2oZqXd5DbQowofFLS2fuk2RaU4tE",
            result
        )
    }

    @Test
    @Throws(Exception::class)
    fun createHDWalletWithPassPhraseTest() {
        val result =
            utils.createHDWallet("length segment syrup visa lava beach rain crush false reveal alone olympic", "TREZOR")

        assertEquals(
            "xprv9s21ZrQH143K4bekgsnc9DtUYZzjjjT9MrcZfQHvKKq7CkifHoAXC58LBFGjjpX6bSyp31mwTtbEMW6NAjV19QaQj6hVpz5Nphr3XiN5fbT",
            result
        )
    }

    @Test
    @Throws(DecoderException::class, NoSuchAlgorithmException::class)
    fun getWalletFromExtKeyTest() {
        val result = utils.getWalletFromExtKey(
            "xprv9s21ZrQH143K4bekgsnc9DtUYZzjjjT9MrcZfQHvKKq7CkifHoAXC58LBFGjjpX6bSyp31mwTtbEMW6NAjV19QaQj6hVpz5Nphr3XiN5fbT",
            1
        )

        assertEquals("H3cpQEhLs3pmwyTnv7PBHmux8CrRBA72d", result[0])
        assertEquals("1a6374f984be521f09a96c4842ec3e66a37e0239b95bd0e13d9632fa8f7dbc4a", result[1])
    }
}