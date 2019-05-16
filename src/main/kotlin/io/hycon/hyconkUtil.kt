package io.hycon

class HyconkUtil {
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
}