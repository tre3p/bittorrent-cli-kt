package bencode.decoder

import bencode.BencodeException
import bencode.decodeBencode
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class BencodeDecoderMapTest {

    @Test
    fun shouldCorrectlyParseBencodedMapFromString() {
        val bencodedMap = "d5:helloi12ei13ei99e4:listl13:somethinghereee"
        val expectedMap = mapOf("hello" to 12, 13 to 99, "list" to listOf("somethinghere"))
        val actualMap = decodeBencode(bencodedMap)[0]

        assertEquals(expectedMap, actualMap)
    }

    @Test
    fun shouldCorrectlyParseBencodedMapFromByteArray() {
        val bencodedMap = "d4:testi10ee".toByteArray()
        val expectedMap = mapOf("test" to 10)
        val actualMap = decodeBencode(bencodedMap)[0]

        assertEquals(expectedMap, actualMap)
    }

    @Test
    fun shouldThrowExceptionOnAmountOfKeysIsBiggerThanValues() {
        val bencodedMap = "d5:helloi13e3:abc4:listl13:somethinghereee"
        assertThrows<BencodeException> { decodeBencode(bencodedMap) }
    }
}
