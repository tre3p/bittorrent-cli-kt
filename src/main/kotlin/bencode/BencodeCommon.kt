package bencode

class BencodeException : Exception {
    constructor(msg: String, cause: Exception) : super(msg, cause)
    constructor(msg: String) : super(msg)
}

object BencoderConstants {
    const val STRING_DELIMITER_BYTE = ':'.code.toByte()
    const val INTEGER_TOKEN_START_BYTE = 'i'.code.toByte()
    const val LIST_TOKEN_START_BYTE = 'l'.code.toByte()
    const val MAP_TOKEN_START_BYTE = 'd'.code.toByte()
    const val END_TOKEN_BYTE = 'e'.code.toByte()
}