package bencode

private const val INTEGER_TOKEN_START_CHAR = 'i'
private const val LIST_TOKEN_START_CHAR = 'l'
private const val MAP_TOKEN_START_CHAR = 'd'
private const val END_TOKEN_CHAR = 'e'

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
    return try {
        when {
            Character.isDigit(bencode[startIndex]) ->
                parseBencodedString(bencode, startIndex).let { Triple(it.first, String::class.java, it.second) }

            bencode[startIndex] == INTEGER_TOKEN_START_CHAR ->
                parseBencodedInteger(bencode, startIndex).let { Triple(it.first, Int::class.java, it.second) }

            bencode[startIndex] == LIST_TOKEN_START_CHAR ->
                parseBencodedList(bencode, startIndex).let { Triple(it.first, List::class.java, it.second) }

            bencode[startIndex] == MAP_TOKEN_START_CHAR ->
                parseBencodedDictionary(bencode, startIndex).let { Triple(it.first, Map::class.java, it.second) }

            else -> throw BencodeParseException("Unknown type of provided bencode!")
        }
    } catch (e: Exception) {
        throw BencodeParseException("Provided bencode is invalid!", e)
    }
}

/**
 * Parses String from 'bencode' starting from 'startIndex'.
 * Returns 'Pair', where first value is parsed String, and the second is end position of parsed token in 'bencode' string.
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
 * Parses Integer from 'bencode' starting from 'startIndex'.
 * Returns 'Pair', where first value is parsed Integer, and the second value is end position of parsed token in 'bencode' string.
 */
private fun parseBencodedInteger(bencode: String, startIndex: Int): Pair<Int, Int> {
    val integerEndIndex = bencode.indexOf(END_TOKEN_CHAR, startIndex)
    val bencodedValue = Integer.parseInt(bencode.substring(startIndex + 1, integerEndIndex))

    return Pair(bencodedValue, integerEndIndex)
}

/**
 * Parses List from 'bencode' starting from 'startIndex'.
 * Returns 'Pair', where first value is parsed List, and the second value is end position of parsed token in 'bencode' string.
 */
private fun parseBencodedList(bencode: String, startIndex: Int): Pair<List<Any>, Int> {
    var currentTokenPosition = startIndex + 1
    val listContent = mutableListOf<Any>()

    while (bencode[currentTokenPosition] != END_TOKEN_CHAR) {
        val (decodedValue, _, tokenEndPosition) = parseSingleBencodeToken(bencode, currentTokenPosition)
        currentTokenPosition = tokenEndPosition + 1
        listContent.add(decodedValue)
    }

    return Pair(listContent, currentTokenPosition)
}

/**
 * Parses Map from 'bencode' starting from 'startIndex'.
 * Return 'Pair', where first value is parsed Map, and the second value is end position of parsed token in 'bencode' string.
 */
private fun parseBencodedDictionary(bencode: String, startIndex: Int): Pair<Map<Any, Any>, Int> {
    var currentTokenPosition = startIndex + 1
    val mapContent = mutableMapOf<Any, Any>()

    while (bencode[currentTokenPosition] != END_TOKEN_CHAR) {
        val (decodedMapKey, _, mapKeyEndPosition) = parseSingleBencodeToken(bencode, currentTokenPosition)
        currentTokenPosition = mapKeyEndPosition + 1

        val (decodedMapValue, _, mapValueEndPosition) = parseSingleBencodeToken(bencode, currentTokenPosition)
        currentTokenPosition = mapValueEndPosition + 1

        mapContent[decodedMapKey] = decodedMapValue
    }

    return Pair(mapContent, currentTokenPosition)
}
