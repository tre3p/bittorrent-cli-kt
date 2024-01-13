package bencode

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BencodeParserIntegerTest {

    @Test
    fun shouldCorrectlyParsePositiveInteger() {
        val actual = parseBencode("i52e")[0]
        assertEquals(52, actual)
    }

    @Test
    fun shouldCorrectlyParseNegativeInteger() {
        val actual = parseBencode("i-23e")[0]
        assertEquals(-23, actual)
    }

    @Test
    fun shouldCorrectlyParseIntegerFromByteArray() {
        val actual = parseBencode("i42e")[0]
        assertEquals(42, actual)
    }
}
