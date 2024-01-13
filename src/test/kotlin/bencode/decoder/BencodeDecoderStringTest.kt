package bencode.decoder

import bencode.BencodeException
import bencode.decodeBencode
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertContentEquals

class BencodeDecoderStringTest {

    @Test
    fun shouldCorrectlyParseBencodedStringToByteArray() {
        val firstActual = decodeBencode("5:hello")[0] as ByteArray
        val firstExpected = "hello".encodeToByteArray()
        assertContentEquals(firstExpected, firstActual)

        val secondActual = decodeBencode("10:hellohello")[0] as ByteArray
        val secondExpected = "hellohello".encodeToByteArray()
        assertContentEquals(secondExpected, secondActual)
    }

    @Test
    fun shouldCorrectlyParseStringFromByteArray() {
        val actual = decodeBencode("4:test".encodeToByteArray())[0] as ByteArray
        val expected = "test".encodeToByteArray()

        assertContentEquals(expected, actual)
    }

    @Test
    fun shouldThrowExceptionOnInvalidStringLengthProvided() {
        assertThrows<BencodeException> { decodeBencode("135:definitelynot135length") }
    }
}
