package bencode

class BencodeParseException : Exception {
    constructor(msg: String, cause: Exception) : super(msg, cause)
    constructor(msg: String) : super(msg)
}

/**
 * Parses bencode string to list of 'Pair'.
 * Accepts 'startIndex' as index from which parsing will be started.
 */
fun parseBencode(bencode: String): List<Pair<Any, Class<*>>> {
    val parsedTokens = mutableListOf<Pair<Any, Class<*>>>()
    var nextTokenPosition = 0

    while (nextTokenPosition < bencode.length) {
        val (decodedToken, tokenType, tokenEndPosition) = parseSingleBencodeToken(bencode, nextTokenPosition)
        nextTokenPosition = tokenEndPosition + 1

        parsedTokens.add(Pair(decodedToken, tokenType))
    }

    return parsedTokens
}

/**
 * Parses single bencode token from provided 'bencode' string starting from 'startIndex'.
 * Returns 'Triple', where first is parsed bencode value, second - it's type and the third is end position of parsed token in 'bencode' string.
 */
private fun parseSingleBencodeToken(bencode: String, startIndex: Int): Triple<Any, Class<*>, Int> {
    try {
        when {
            // If first character is integer - it's a bencoded string type
            Character.isDigit(bencode[startIndex]) -> {
                val (decodedToken, tokenEndPosition) = parseBencodedString(bencode, startIndex)
                return Triple(decodedToken, String::class.java, tokenEndPosition)
            }

            // If first character is 'i' - it's a bencoded integer type
            bencode[startIndex] == 'i' -> {
                val (decodedToken, tokenEndPosition) = parseBencodedInteger(bencode, startIndex)
                return Triple(decodedToken, Int::class.java, tokenEndPosition)
            }

            bencode[startIndex] == 'l' -> {
                val (decodedToken, tokenEndPosition) = parseBencodedList(bencode, startIndex)
                return Triple(decodedToken, List::class.java, tokenEndPosition)
            }
        }
    } catch (e: Exception) {
        throw BencodeParseException("Provided bencode is invalid!", e)
    }

    throw BencodeParseException("Unknown type of provided bencode!")
}

/**
 * Parses string from 'bencode' starting from 'startIndex'.
 * Returns 'Pair', where first value is parsed string, and the second is end position of parsed token in 'bencode' string.
 */
private fun parseBencodedString(bencode: String, startIndex: Int): Pair<String, Int> {
    val firstColonIndex = bencode.indexOf(':', startIndex)
    val bencodeStringSize = Integer.parseInt(bencode.substring(startIndex, firstColonIndex))

    val stringStartIndex = firstColonIndex + 1
    val stringEndIndex = (stringStartIndex + bencodeStringSize) - 1

    if (stringEndIndex > bencode.length) {
        throw BencodeParseException("Bencoded string end index is bigger than string length")
    }

    val bencodedValue = bencode.substring(stringStartIndex..stringEndIndex)
    return Pair(bencodedValue, stringEndIndex)
}

/**
 * Parses integer from 'bencode' starting from 'startIndex'.
 * Returns 'Pair', where first value is parsed integer, and the second value is end position of parsed token in 'bencode' string.
 */
private fun parseBencodedInteger(bencode: String, startIndex: Int): Pair<Int, Int> {
    val integerEndIndex = bencode.indexOf('e', startIndex)
    val bencodedValue = Integer.parseInt(bencode.substring(startIndex + 1, integerEndIndex))

    return Pair(bencodedValue, integerEndIndex)
}

private fun parseBencodedList(bencode: String, startIndex: Int): Pair<List<Any>, Int> {
    var currentTokenPosition = startIndex + 1
    val listContent = mutableListOf<Any>()

    while (bencode[currentTokenPosition] != 'e') {
        val (decodedValue, _, tokenEndPosition) = parseSingleBencodeToken(bencode, currentTokenPosition)
        currentTokenPosition = tokenEndPosition + 1
        listContent.add(decodedValue)
    }

    return Pair(listContent, currentTokenPosition)
}

private fun parseBencodedDictionary(bencode: String): Map<String, Pair<Any, Class<*>>> {
    return mapOf()
}
