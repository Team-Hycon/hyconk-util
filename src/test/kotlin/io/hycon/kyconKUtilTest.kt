package io.hycon

import org.junit.Assert.assertEquals
import org.junit.Test

class HyconKUtilTest {
    @Test fun testHyconFromString() {
        assertEquals(0L, HyconkUtil().hyconFromString(""))
        assertEquals(1L,HyconkUtil().hyconFromString("0.000000001"))
        assertEquals(0L,HyconkUtil().hyconFromString(null))
        assertEquals(1000000000L, HyconkUtil().hyconFromString("1"))
        assertEquals(1000000000L, HyconkUtil().hyconFromString("1."))
        assertEquals(1010000000L, HyconkUtil().hyconFromString("1.01"))
    }

    @Test fun testHyconToString() {
        assertEquals("0", HyconkUtil().hyconToString(0L))
        assertEquals("0.000000001",HyconkUtil().hyconToString(1L))
        assertEquals("1", HyconkUtil().hyconToString(1000000000L))
        assertEquals("1.0101", HyconkUtil().hyconToString(1010100000L))
    }
}