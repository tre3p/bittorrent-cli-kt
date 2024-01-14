package bencode.encoder

import bencode.encodeToBencode
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals

class BencodeEncoderListTest {

    @Test
    fun shouldCorrectlyParsePlainList() {
        val list = listOf("hello", 5, "bencode")
        val expected = "l5:helloi5e7:bencodee".encodeToByteArray()
        val actual = encodeToBencode(list)

        assertContentEquals(expected, actual)
    }

    @Test
    fun shouldCorrectlyParseNestedList() {
        val list = listOf("bencode", listOf(42, "test"), mapOf("mg" to 42))
        val expected = "l7:bencodeli42e4:tested2:mgi42eee".encodeToByteArray()
        val actual = encodeToBencode(list)

        assertContentEquals(expected, actual)
    }

}