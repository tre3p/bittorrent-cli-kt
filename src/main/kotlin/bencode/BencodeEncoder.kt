package bencode

import bencode.BencoderConstants.END_TOKEN_CHAR
import bencode.BencoderConstants.INTEGER_TOKEN_START_CHAR
import bencode.BencoderConstants.LIST_TOKEN_START_CHAR
import bencode.BencoderConstants.MAP_TOKEN_START_CHAR
import bencode.BencoderConstants.STRING_DELIMITER_CHAR
import java.lang.StringBuilder

fun encodeToBencode(obj: Any): ByteArray {
    return when (obj) {
        is String -> encodeString(obj)
        is Int -> encodeInt(obj)
        is ByteArray -> encodeString(String(obj))
        is List<*> -> encodeList(obj as List<Any>)
        is Map<*, *> -> encodeMap(obj as Map<Any, Any>)
        else -> throw BencodeException("Unknown token type!")
    }.encodeToByteArray()
}

private fun encodeString(str: String): String {
    return "${str.length}$STRING_DELIMITER_CHAR$str"
}

private fun encodeInt(int: Int): String {
    return "$INTEGER_TOKEN_START_CHAR$int$END_TOKEN_CHAR"
}

private fun encodeList(list: List<Any>): String {
    val sb = StringBuilder()
    sb.append(LIST_TOKEN_START_CHAR)

    for (obj in list) {
        sb.append(String(encodeToBencode(obj)))
    }

    sb.append(END_TOKEN_CHAR)

    return sb.toString()
}

private fun encodeMap(map: Map<Any, Any>): String {
    val sb = StringBuilder()
    sb.append(MAP_TOKEN_START_CHAR)

    for (entry in map.entries.iterator()) {
        sb.append(String(encodeToBencode(entry.key)))
        sb.append(String(encodeToBencode(entry.value)))
    }

    sb.append(END_TOKEN_CHAR)

    return sb.toString()
}
