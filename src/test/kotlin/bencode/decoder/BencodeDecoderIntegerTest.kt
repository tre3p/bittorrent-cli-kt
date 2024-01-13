package bencode.decoder

import bencode.decodeBencode
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BencodeDecoderIntegerTest {

    @Test
    fun shouldCorrectlyParsePositiveInteger() {
        val actual = decodeBencode("i52e")[0]
        assertEquals(52, actual)
    }

    @Test
    fun shouldCorrectlyParseNegativeInteger() {
        val actual = decodeBencode("i-23e")[0]
        assertEquals(-23, actual)
    }

    @Test
    fun shouldCorrectlyParseIntegerFromByteArray() {
        val actual = decodeBencode("i42e")[0]
        assertEquals(42, actual)
    }
}
