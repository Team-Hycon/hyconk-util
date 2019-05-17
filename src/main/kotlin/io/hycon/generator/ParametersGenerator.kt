package io.hycon.generator

import org.bouncycastle.crypto.CipherParameters
import org.bouncycastle.crypto.Digest
import org.bouncycastle.crypto.PBEParametersGenerator
import org.bouncycastle.crypto.digests.SHA1Digest
import org.bouncycastle.crypto.macs.HMac
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.crypto.params.ParametersWithIV
import org.bouncycastle.util.Arrays



class ParametersGenerator @JvmOverloads constructor(digest: Digest = SHA1Digest()) : PBEParametersGenerator() {
    private val hMac: HMac
    private val state: ByteArray

    init {
        hMac = HMac(digest)
        state = ByteArray(hMac.getMacSize())
    }

    private fun F(
        S: ByteArray?,
        c: Int,
        iBuf: ByteArray,
        out: ByteArray,
        outOff: Int
    ) {
        if (c == 0) {
            throw IllegalArgumentException("iteration count must be at least 1.")
        }

        if (S != null) {
            hMac.update(S, 0, S.size)
        }

        hMac.update(iBuf, 0, iBuf.size)
        hMac.doFinal(state, 0)

        System.arraycopy(state, 0, out, outOff, state.size)

        for (count in 1 until c) {
            hMac.update(state, 0, state.size)
            hMac.doFinal(state, 0)

            for (j in state.indices) out[outOff + j] = (out[outOff + j].toInt() xor state[j].toInt()).toByte()
        }
    }

    private fun generateDerivedKey(
        dkLen: Int
    ): ByteArray {
        val hLen = hMac.getMacSize()
        val l = (dkLen + hLen - 1) / hLen
        val iBuf = ByteArray(4)
        val outBytes = ByteArray(l * hLen)
        var outPos = 0

        val param = KeyParameter(password)

        hMac.init(param)

        for (i in 1..l) {
            // Increment the value in 'iBuf'
            var pos = 3
            while ((++iBuf[pos]).toInt() == 0) {
                --pos
            }

            F(salt, iterationCount, iBuf, outBytes, outPos)
            outPos += hLen
        }

        return outBytes
    }

    /**
     * Generate a key parameter derived from the password, salt, and iteration
     * count we are currently initialised with.
     *
     * @param keySize the size of the key we want (in bits)
     * @return a KeyParameter object.
     */
    override fun generateDerivedParameters(
        keySize: Int
    ): CipherParameters {
        var keySize = keySize
        keySize = keySize / 8

        val dKey = Arrays.copyOfRange(generateDerivedKey(keySize), 0, keySize)

        return KeyParameter(dKey, 0, keySize)
    }

    /**
     * Generate a key with initialisation vector parameter derived from
     * the password, salt, and iteration count we are currently initialised
     * with.
     *
     * @param keySize the size of the key we want (in bits)
     * @param ivSize the size of the iv we want (in bits)
     * @return a ParametersWithIV object.
     */
    override fun generateDerivedParameters(
        keySize: Int,
        ivSize: Int
    ): CipherParameters {
        var keySize = keySize
        var ivSize = ivSize
        keySize = keySize / 8
        ivSize = ivSize / 8

        val dKey = generateDerivedKey(keySize + ivSize)

        return ParametersWithIV(KeyParameter(dKey, 0, keySize), dKey, keySize, ivSize)
    }

    /**
     * Generate a key parameter for use with a MAC derived from the password,
     * salt, and iteration count we are currently initialised with.
     *
     * @param keySize the size of the key we want (in bits)
     * @return a KeyParameter object.
     */
    override fun generateDerivedMacParameters(
        keySize: Int
    ): CipherParameters {
        return generateDerivedParameters(keySize)
    }
}
/**
 * construct a PKCS5 Scheme 2 Parameters generator.
 */