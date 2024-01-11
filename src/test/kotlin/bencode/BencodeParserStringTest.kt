package bencode

import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class BencodeParserStringTest {

    @Test
    fun shouldCorrectlyParseBencodedString() {
        val actual = parseBencode("5:hello").first
        assertEquals("hello", actual)
    }

    @Test
    fun shouldExtractExactCountOfCharsFromString() {
        val actual = parseBencode("2:test").first
        assertEquals("te", actual)
    }

    @Test
    fun shouldThrowExceptionOnInvalidStringLengthProvided() {
        assertThrows<BencodeParseException> { parseBencode("135:definitelynot135length") }
    }

}