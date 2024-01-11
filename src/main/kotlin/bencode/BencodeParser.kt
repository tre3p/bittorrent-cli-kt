package bencode

class BencodeParseException : Exception {
    constructor(msg: String, cause: Exception): super(msg, cause)
    constructor(msg: String) : super(msg)
}

/**
 * Parses exactly one bencode token.
 * Returns Pair object with bencode value as first parameter and it's type as second parameter
 */
fun parseBencode(bencode: String): Pair<Any, Class<*>> {
    try {
        when {
            Character.isDigit(bencode[0]) -> {
                return Pair(parseBencodedString(bencode), String::class.java)
            }
        }
    } catch (e: Exception) {
        throw BencodeParseException("Provided bencode is invalid!", e)
    }

    throw BencodeParseException("Unknown type of provided bencode!")
}

private fun parseBencodedString(bencodedString: String): String {
    val firstColonIndex = bencodedString.indexOfFirst { it == ':' }
    val stringSizeIntegerLength = Integer.parseInt(bencodedString.substring(0, firstColonIndex))

    val stringStartIndex = firstColonIndex + 1
    val stringEndIndex = stringStartIndex + stringSizeIntegerLength

    if (stringEndIndex > bencodedString.length) {
        throw BencodeParseException("Bencoded string end index is bigger than string length")
    }

    return bencodedString.substring(stringStartIndex, stringEndIndex)
}

private fun parseBencodedInteger(bencodedInteger: String): Int {
    return 0
}

private fun parseBencodedList(bencodedList: String): List<Any> {
    return mutableListOf()
}

private fun parseBencodedDictionary(bencodedDictionary: String): Map<String, Pair<Any, Class<*>>> {
    return mapOf()
}