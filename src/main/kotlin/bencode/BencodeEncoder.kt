package bencode

import bencode.BencoderConstants.END_TOKEN_BYTE
import bencode.BencoderConstants.INTEGER_TOKEN_START_BYTE
import bencode.BencoderConstants.LIST_TOKEN_START_BYTE
import bencode.BencoderConstants.MAP_TOKEN_START_BYTE
import bencode.BencoderConstants.STRING_DELIMITER_BYTE

fun encodeToBencode(obj: Any): ByteArray {
    return when (obj) {
        is String -> encodeString(obj.encodeToByteArray())
        is ByteArray -> encodeString(obj)
        is Int -> encodeInt(obj)
        is ByteArray -> encodeString(String(obj))
        is List<*> -> encodeList(obj as List<Any>)
        is Map<*, *> -> encodeMap(obj as Map<Any, Any>)
        else -> throw BencodeException("Unknown token type!")
    }
}

private fun encodeString(str: ByteArray): ByteArray {
    val stringBytes = mutableListOf<Byte>()
    stringBytes.addAll(str.size.toString().encodeToByteArray().asList())
    stringBytes.add(STRING_DELIMITER_BYTE)
    stringBytes.addAll(str.asList())

    return stringBytes.toByteArray()
}

private fun encodeInt(int: Int): ByteArray {
    val intBytes = mutableListOf<Byte>()
    intBytes.add(INTEGER_TOKEN_START_BYTE)
    intBytes.addAll(int.toString().encodeToByteArray().asList())
    intBytes.add(END_TOKEN_BYTE)

    return intBytes.toByteArray()
}

private fun encodeList(list: List<Any>): ByteArray {
    val listBytes = mutableListOf<Byte>()
    listBytes.add(LIST_TOKEN_START_BYTE)

    for (obj in list) {
        listBytes.addAll(encodeToBencode(obj).asList())
    }

    listBytes.add(END_TOKEN_BYTE)

    return listBytes.toByteArray()
}

private fun encodeMap(map: Map<Any, Any>): ByteArray {
    val mapBytes = mutableListOf<Byte>()
    mapBytes.add(MAP_TOKEN_START_BYTE)

    for (entry in map.entries.iterator()) {
        mapBytes.addAll(encodeToBencode(entry.key).asList())
        mapBytes.addAll(encodeToBencode(entry.value).asList())
    }

    mapBytes.add(END_TOKEN_BYTE)

    return mapBytes.toByteArray()
}
