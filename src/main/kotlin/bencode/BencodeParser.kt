package bencode

import extensions.firstIndexOf

private const val STRING_DELIMITER_BYTE = ':'.code.toByte()
private const val INTEGER_TOKEN_START_BYTE = 'i'.code.toByte()
private const val LIST_TOKEN_START_BYTE = 'l'.code.toByte()
private const val MAP_TOKEN_START_BYTE = 'd'.code.toByte()
private const val END_TOKEN_BYTE = 'e'.code.toByte()

class BencodeParseException : Exception {
    constructor(msg: String, cause: Exception) : super(msg, cause)
    constructor(msg: String) : super(msg)
}

/**
 * Parses bencode string to list of 'Pair', where first is decoded value, and the second its type.
 */
fun parseBencode(bencode: String): List<Pair<Any, Class<*>>> {
    return parseBencode(bencode.toByteArray())
}

/**
 * Parses bencoded byte array (ASCII chars) to list of 'Pair', where first is decoded value, and the second its type.
 */
fun parseBencode(bencode: ByteArray): List<Pair<Any, Class<*>>> {
    val parsedTokens = mutableListOf<Pair<Any, Class<*>>>()
    var nextTokenPosition = 0

    while (nextTokenPosition < bencode.size) {
        val (decodedToken, tokenType, tokenEndPosition) = parseSingleBencodeToken(bencode, nextTokenPosition)
        nextTokenPosition = tokenEndPosition + 1

        parsedTokens.add(Pair(decodedToken, tokenType))
    }

    return parsedTokens
}

/**
 * Parses single bencode token from provided 'bencode' byte array starting from 'startIndex'.
 * Returns 'Triple', where first is parsed bencode value, second - it's type and the third is end position of parsed token in 'bencode' byte array.
 */
private fun parseSingleBencodeToken(bencode: ByteArray, startIndex: Int): Triple<Any, Class<*>, Int> {
    val byteToChar = Char(bencode[startIndex].toInt())

    return try {
        when {
            Character.isDigit(byteToChar) ->
                parseBencodedString(bencode, startIndex).let { Triple(it.first, ByteArray::class.java, it.second) }

            bencode[startIndex] == INTEGER_TOKEN_START_BYTE ->
                parseBencodedInteger(bencode, startIndex).let { Triple(it.first, Int::class.java, it.second) }

            bencode[startIndex] == LIST_TOKEN_START_BYTE ->
                parseBencodedList(bencode, startIndex).let { Triple(it.first, List::class.java, it.second) }

            bencode[startIndex] == MAP_TOKEN_START_BYTE ->
                parseBencodedDictionary(bencode, startIndex).let { Triple(it.first, Map::class.java, it.second) }

            else -> throw BencodeParseException("Unknown type of provided bencode!")
        }
    } catch (e: Exception) {
        throw BencodeParseException("Provided bencode is invalid!", e)
    }
}

/**
 * Parses string from 'bencode' to ByteArray starting from 'startIndex'.
 * Returns 'Pair', where first value is parsed string, and the second is end position of parsed token in 'bencode' byte array.
 */
private fun parseBencodedString(bencode: ByteArray, startIndex: Int): Pair<ByteArray, Int> {
    val firstColonIndex = bencode.firstIndexOf(STRING_DELIMITER_BYTE, startIndex)
    val bencodeStringSize = Integer.parseInt(String(bencode.copyOfRange(startIndex, firstColonIndex)))

    val stringStartIndex = firstColonIndex + 1
    val stringEndIndex = stringStartIndex + bencodeStringSize

    if (stringEndIndex > bencode.size) {
        throw BencodeParseException("Bencoded array end index is bigger than array size")
    }

    val bencodedValue = bencode.copyOfRange(stringStartIndex, stringEndIndex)

    return Pair(bencodedValue, stringEndIndex - 1)
}

/**
 * Parses integer from 'bencode' starting from 'startIndex'.
 * Returns 'Pair', where first value is parsed integer, and the second value is end position of parsed token in 'bencode' byte array.
 */
private fun parseBencodedInteger(bencode: ByteArray, startIndex: Int): Pair<Int, Int> {
    val integerEndIndex = bencode.firstIndexOf(END_TOKEN_BYTE, startIndex)
    val bencodedValue = Integer.parseInt(String(bencode.copyOfRange(startIndex + 1, integerEndIndex)))

    return Pair(bencodedValue, integerEndIndex)
}

/**
 * Parses list from 'bencode' starting from 'startIndex'.
 * Returns 'Pair', where first value is parsed list, and the second value is end position of parsed token in 'bencode' byte array.
 */
private fun parseBencodedList(bencode: ByteArray, startIndex: Int): Pair<List<Any>, Int> {
    var currentTokenPosition = startIndex + 1
    val listContent = mutableListOf<Any>()

    while (bencode[currentTokenPosition] != END_TOKEN_BYTE) {
        val (decodedValue, decodedValueType, tokenEndPosition) = parseSingleBencodeToken(bencode, currentTokenPosition)
        currentTokenPosition = tokenEndPosition + 1

        if (decodedValueType == ByteArray::class.java) {
            listContent.add(String(decodedValue as ByteArray))
        } else {
            listContent.add(decodedValue)
        }
    }

    return Pair(listContent, currentTokenPosition)
}

/**
 * Parses Map from 'bencode' starting from 'startIndex'.
 * Return 'Pair', where first value is parsed Map, and the second value is end position of parsed token in 'bencode' byte array.
 */
private fun parseBencodedDictionary(bencode: ByteArray, startIndex: Int): Pair<Map<Any, Any>, Int> {
    var currentTokenPosition = startIndex + 1
    val resultMap = mutableMapOf<Any, Any>()

    while (bencode[currentTokenPosition] != END_TOKEN_BYTE) {
        val (decodedMapKey, mapKeyType, mapKeyEndPosition) = parseSingleBencodeToken(bencode, currentTokenPosition)
        currentTokenPosition = mapKeyEndPosition + 1

        val (decodedMapValue, _, mapValueEndPosition) = parseSingleBencodeToken(bencode, currentTokenPosition)
        currentTokenPosition = mapValueEndPosition + 1

        /*
        * If parsed map key is byte array - store it as a string key instead of byte array to allow user
        * get values using string literal instead of byte array string representation.
        * Decoded map values must stay the same, because some of bencoded values are not valid UTF characters.
        *
        * TODO: rewrite this
        */
        if (mapKeyType == ByteArray::class.java) {
            resultMap[String(decodedMapKey as ByteArray)] = decodedMapValue
        } else {
            resultMap[decodedMapKey] = decodedMapValue
        }
    }

    return Pair(resultMap, currentTokenPosition)
}