package bencode.encoder

import bencode.encodeToBencode
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals

class BencodeEncoderMapTest {

    @Test
    fun shouldCorrectlyEncodePlainMap() {
        val map = mapOf("str" to 2, 23 to "test")
        val expected = "d3:stri2ei23e4:teste".encodeToByteArray()
        val actual = encodeToBencode(map)

        assertContentEquals(expected, actual)
    }

    @Test
    fun shouldCorrectlyEncodeNestedMap() {
        val map = mapOf("abc" to listOf(1, 2), "test" to mapOf(1 to "bencode"))
        val expected = "d3:abcli1ei2ee4:testdi1e7:bencodeee".encodeToByteArray()
        val actual = encodeToBencode(map)

        assertContentEquals(expected, actual)
    }
}
