package bencode

import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertContentEquals

class BencodeParserStringTest {

    @Test
    fun shouldCorrectlyParseBencodedStringToByteArray() {
        val firstActual = parseBencode("5:hello")[0] as ByteArray
        val firstExpected = "hello".encodeToByteArray()
        assertContentEquals(firstExpected, firstActual)

        val secondActual = parseBencode("10:hellohello")[0] as ByteArray
        val secondExpected = "hellohello".encodeToByteArray()
        assertContentEquals(secondExpected, secondActual)
    }

    @Test
    fun shouldCorrectlyParseStringFromByteArray() {
        val actual = parseBencode("4:test".encodeToByteArray())[0] as ByteArray
        val expected = "test".encodeToByteArray()

        assertContentEquals(expected, actual)
    }

    @Test
    fun shouldThrowExceptionOnInvalidStringLengthProvided() {
        assertThrows<BencodeParseException> { parseBencode("135:definitelynot135length") }
    }
}
