package io.hycon

data class Language(val name: String, val hotness: Int)

class HyconkUtil {
    /**
     * @return data relating to the Kotlin {@code Lanugage}.
     */
    fun kotlinLanguage() = Language("Kotlin", 10)
}