package bencode

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BencodeParserIntegerTest {

    @Test
    fun shouldCorrectlyParsePositiveInteger() {
        val actual = parseBencode("i52e")[0].first
        assertEquals(52, actual)
    }

    @Test
    fun shouldCorrectlyParseNegativeInteger() {
        val actual = parseBencode("i-23e")[0].first
        assertEquals(-23, actual)
    }
}
