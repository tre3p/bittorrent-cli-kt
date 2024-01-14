package bencode

class BencodeException : Exception {
    constructor(msg: String, cause: Exception) : super(msg, cause)
    constructor(msg: String) : super(msg)
}

object BencoderConstants {
    const val STRING_DELIMITER_CHAR = ':'
    const val INTEGER_TOKEN_START_CHAR = 'i'
    const val LIST_TOKEN_START_CHAR = 'l'
    const val MAP_TOKEN_START_CHAR = 'd'
    const val END_TOKEN_CHAR = 'e'

    const val STRING_DELIMITER_BYTE = STRING_DELIMITER_CHAR.code.toByte()
    const val INTEGER_TOKEN_START_BYTE = INTEGER_TOKEN_START_CHAR.code.toByte()
    const val LIST_TOKEN_START_BYTE = LIST_TOKEN_START_CHAR.code.toByte()
    const val MAP_TOKEN_START_BYTE = MAP_TOKEN_START_CHAR.code.toByte()
    const val END_TOKEN_BYTE = END_TOKEN_CHAR.code.toByte()
}
