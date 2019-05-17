package io.hycon

import com.rfksystems.blake2b.Blake2b
import com.rfksystems.blake2b.security.Blake2bProvider
import io.hycon.generator.MnemonicGenerator
import io.hycon.mnemonic.English
import io.hycon.mnemonic.Korean
import org.bitcoinj.core.AddressFormatException
import org.bitcoinj.core.Base58
import org.bitcoinj.core.ECKey
import org.bouncycastle.util.encoders.DecoderException
import ru.d_shap.hex.HexHelper
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.Security.addProvider
import org.bitcoinj.crypto.HDKeyDerivation
import org.bitcoinj.crypto.ChildNumber
import org.bitcoinj.crypto.DeterministicKey






class HyconkUtil {

    init {
        addProvider(Blake2bProvider())
    }

    fun encodeHexByteArrayToString(ob: ByteArray): String {
        return HexHelper.toHex(ob)
    }

    @Throws(DecoderException::class)
    fun decodeHexStringToByteArray(str: String): ByteArray {
        return HexHelper.toBytes(str)
    }

    @Throws(NoSuchAlgorithmException::class)
    fun blake2bHash(ob: ByteArray): ByteArray {
        return MessageDigest.getInstance(Blake2b.BLAKE2_B_256).digest(ob)
    }

    @Throws(NoSuchAlgorithmException::class, DecoderException::class)
    fun blake2bHash(ob: String): ByteArray {
        return MessageDigest.getInstance(Blake2b.BLAKE2_B_256).digest(HexHelper.toBytes(ob))
    }

    fun base58Encode(ob: ByteArray): String {
        return Base58.encode(ob)
    }

    @Throws(AddressFormatException::class)
    fun base58Decode(ob: String): ByteArray {
        return Base58.decode(ob)
    }

    @Throws(NoSuchAlgorithmException::class)
    fun publicKeyToAddress(publicKey: ByteArray): ByteArray {
        val hash = blake2bHash(publicKey)
        val result = ByteArray(20)
        for (i in 12..31) {
            result[i - 12] = hash[i]
        }

        return result
    }

    @Throws(NoSuchAlgorithmException::class)
    fun addresssCheckSum(arr: ByteArray): String {
        val hash = blake2bHash(arr)
        var str = base58Encode(hash)
        str = str.substring(0, 4)
        return str
    }

    @Throws(NoSuchAlgorithmException::class)
    fun addressToString(publicKey: ByteArray): String {
        return "H${base58Encode(publicKey)}${addresssCheckSum(publicKey)}"
    }

    @Throws(Exception::class)
    fun addressToByteArray(address: String): ByteArray {
        var address = address
        if (address[0] != 'H') {
            throw Exception("Address is invalid. Expected address to start with 'H'")
        }

        val checkSum = address.substring(address.length - 4, address.length)
        address = address.substring(1, address.length - 4)
        val out = base58Decode(address)

        if (out.size != 20) {
            throw Exception("Address must be 20 bytes long")
        }

        val expectChecksum = addresssCheckSum(out)
        if (expectChecksum != checkSum) {
            throw Exception("Address hash invalid checksum $checkSum expected $expectChecksum")
        }

        return out

    }

    fun hyconFromString(valueString: String?):Long {
        if (valueString == "" || valueString == null) {
            return 0L
        } else {
            var hycString = valueString
            if (valueString.endsWith('.')) {
                hycString+="0"
            }
            var splitArr = hycString.split('.')
            var hyc = 0L
            hyc += splitArr[0].toLong()*1000000000L
            if (splitArr.size > 1) {
                val exp = (9-splitArr[1].length).toDouble()
                hyc += splitArr[1].toLong() * (Math.pow(10.0,exp)).toLong()
            }
            return hyc
        }
    }

    fun hyconToString(value: Long):String {
        val units = value/1000000000L
        val dec = value%1000000000L
        if (dec == 0L) {
            return "$units"
        }
        var digString = "$dec"
        return "$units.${"0".repeat(9 - digString.length)}${digString.trimEnd('0')}"
    }

    @Throws(IOException::class)
    fun getMnemonic(language: String): String {

        val wordList = getBip39WordList(language)

        return MnemonicGenerator.generateMnemonic(wordList)
    }

    private fun getBip39WordList(language: String): Array<String> {
        return if (language == "english") {
            English.words
        } else if (language == "korean") {
            Korean.words }
//        } else if (language == "chinese_simplified") {
//            ChineseSimplified.words
//        } else if (language == "chinese_traditional") {
//            ChineseTraditional.words
//        } else if (language == "chinese") {
//            throw Error("Did you mean chinese_simplified or chinese_traditional?")
//        } else if (language == "japanese") {
//            Japanese.words
//        } else if (language == "french") {
//            French.words
//        } else if (language == "spanish") {
//            Spanish.words
//        } else if (language == "italian") {
//            Italian.words
//        }
        else {
            English.words
        }
    }

    private fun fromBIP44HDPath(master: DeterministicKey, accountIndex: Int): ECKey {
        val purposeKey = HDKeyDerivation.deriveChildKey(master, 44 or ChildNumber.HARDENED_BIT)
        val rootKey = HDKeyDerivation.deriveChildKey(purposeKey, 1397 or ChildNumber.HARDENED_BIT)
        val accountKey = HDKeyDerivation.deriveChildKey(rootKey, 0 or ChildNumber.HARDENED_BIT)
        val changeKey = HDKeyDerivation.deriveChildKey(accountKey, 0)
        val addressKey = HDKeyDerivation.deriveChildKey(changeKey, accountIndex)

        return ECKey.fromPrivate(addressKey.privKeyBytes)
    }
}