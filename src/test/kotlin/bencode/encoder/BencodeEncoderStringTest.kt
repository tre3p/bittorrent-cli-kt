package bencode.encoder

import bencode.encodeToBencode
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals

class BencodeEncoderStringTest {

    @Test
    fun shouldCorrectlyEncodeSingleString() {
        val str = "hello"
        val expected = "5:hello".encodeToByteArray()
        val actual = encodeToBencode(str)

        assertContentEquals(expected, actual)
    }
}
