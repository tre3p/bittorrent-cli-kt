package bencode.encoder

import bencode.encodeToBencode
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals

class BencodeEncoderIntegerTest {

    @Test
    fun shouldCorrectlyEncodeSingleInteger() {
        val int = 5
        val expected = "i5e".encodeToByteArray()
        val actual = encodeToBencode(int)

        assertContentEquals(expected, actual)
    }
}
