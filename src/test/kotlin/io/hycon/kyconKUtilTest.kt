package io.hycon

import org.junit.Assert.assertEquals
import org.junit.Test

class HyconKUtilTest {
    @Test fun testMyLanguage() {
        assertEquals("Kotlin", HyconkUtil().kotlinLanguage().name)
        assertEquals(10, HyconkUtil().kotlinLanguage().hotness)
    }
}