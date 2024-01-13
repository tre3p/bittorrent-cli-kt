package bencode.encoder

import bencode.encodeToBencode
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class BencodeEncoderIntegerTest {

    @Test
    fun shouldCorrectlyEncodeSingleInteger() {
        val int = 5
        val expected = "i5e".encodeToByteArray()
        val actual = encodeToBencode(int)

        assertContentEquals(expected, actual)
    }

    @Test
    fun shouldCorrectlyEncodeMultipleIntegers() {
        val ints = listOf(5, 4, -3, 2)
        val expected = "i5ei4ei-3ei2e".encodeToByteArray()
        val actual = encodeToBencode(ints)

        assertContentEquals(expected, actual)
    }

}