package bencode

import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertContentEquals

class BencodeParserStringTest {

    @Test
    fun shouldCorrectlyParseBencodedStringToByteArray() {
        val firstActual = parseBencode("5:hello")[0].first as ByteArray
        val firstExpected = "hello".encodeToByteArray()
        assertContentEquals(firstExpected, firstActual)

        val secondActual = parseBencode("10:hellohello")[0].first as ByteArray
        val secondExpected = "hellohello".encodeToByteArray()
        assertContentEquals(secondExpected, secondActual)
    }

    @Test
    fun shouldThrowExceptionOnInvalidStringLengthProvided() {
        assertThrows<BencodeParseException> { parseBencode("135:definitelynot135length") }
    }
}
