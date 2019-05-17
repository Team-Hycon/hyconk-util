package io.hycon.generator

import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*

import org.bouncycastle.crypto.digests.SHA512Digest
import org.bouncycastle.crypto.params.KeyParameter

object MnemonicGenerator {
    private const val SEED_ITERATIONS = 2048
    private const val SEED_KEY_SIZE = 512

    fun generateMnemonic(wordList: Array<String>): String {

        val secureRandom = SecureRandom()
        val initialEntropy = ByteArray(16)
        secureRandom.nextBytes(initialEntropy)

        validateInitialEntropy(initialEntropy)

        val ent = initialEntropy.size * 8
        val checksumLength = ent / 32

        val checksum = calculateChecksum(initialEntropy)
        val bits = convertToBits(initialEntropy, checksum)

        val iterations = (ent + checksumLength) / 11
        val mnemonicBuilder = StringBuilder()
        for (i in 0 until iterations) {
            val index = toInt(nextElevenBits(bits, i))
            mnemonicBuilder.append(wordList[index])

            val notLastIteration = i < iterations - 1
            if (notLastIteration) {
                mnemonicBuilder.append(" ")
            }
        }

        return mnemonicBuilder.toString()
    }

    private fun convertToBits(initialEntropy: ByteArray, checksum: Byte): BooleanArray {
        val ent = initialEntropy.size * 8
        val checksumLength = ent / 32
        val totalLength = ent + checksumLength
        val bits = BooleanArray(totalLength)

        for (i in initialEntropy.indices) {
            for (j in 0..7) {
                val b = initialEntropy[i]
                bits[8 * i + j] = toBit(b, j)
            }
        }

        for (i in 0 until checksumLength) {
            bits[ent + i] = toBit(checksum, i)
        }

        return bits
    }

    private fun calculateChecksum(initialEntropy: ByteArray): Byte {
        val ent = initialEntropy.size * 8
        val mask = (0xff shl 8 - ent / 32).toByte()
        val bytes = MessageDigest.getInstance("sha-256").digest(initialEntropy)
        return (bytes[0].toInt() and mask.toInt()).toByte()
    }

    private fun validateInitialEntropy(initialEntropy: ByteArray?) {
        if (initialEntropy == null) {
            throw IllegalArgumentException("Initial entropy is required")
        }

        val ent = initialEntropy.size * 8
        if (ent < 128 || ent > 256 || ent % 32 != 0) {
            throw IllegalArgumentException("The allowed size of ENT is 128-256 bits of " + "multiples of 32")
        }
    }

    private fun toBit(value: Byte, index: Int): Boolean {
        return value.toInt() ushr (7 - index) and 1 > 0
    }

    private fun toInt(bits: BooleanArray): Int {
        var value = 0
        for (i in bits.indices) {
            val isSet = bits[i]
            if (isSet) {
                value += 1 shl bits.size - i - 1
            }
        }

        return value
    }

    private fun nextElevenBits(bits: BooleanArray, i: Int): BooleanArray {
        val from = i * 11
        val to = from + 11
        return Arrays.copyOfRange(bits, from, to)
    }

    fun generateSeed(mnemonic: String, passphrase: String?): ByteArray {
        var passphrase = passphrase
        validateMnemonic(mnemonic)
        passphrase = passphrase ?: ""

        val salt = String.format("mnemonic%s", passphrase)
        val gen = ParametersGenerator(SHA512Digest())
        gen.init(
            mnemonic.toByteArray(Charset.forName("UTF-8")),
            salt.toByteArray(Charset.forName("UTF-8")),
            SEED_ITERATIONS
        )

        return (gen.generateDerivedParameters(SEED_KEY_SIZE) as KeyParameter).getKey()
    }

    private fun validateMnemonic(mnemonic: String?) {
        if (mnemonic == null || mnemonic.trim { it <= ' ' }.isEmpty()) {
            throw IllegalArgumentException("Mnemonic is required to generate a seed")
        }
    }
}
