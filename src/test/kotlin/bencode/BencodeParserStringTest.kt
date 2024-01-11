package bencode

import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class BencodeParserStringTest {

    @Test
    fun shouldCorrectlyParseBencodedString() {
        val firstActual = parseBencode("5:hello")[0].first
        val firstExpected = "hello"
        assertEquals(firstExpected, firstActual)

        val secondActual = parseBencode("10:hellohello")[0].first
        var secondExpected = "hellohello"
        assertEquals(secondExpected, secondActual)
    }

    @Test
    fun shouldThrowExceptionOnInvalidStringLengthProvided() {
        assertThrows<BencodeParseException> { parseBencode("135:definitelynot135length") }
    }
}