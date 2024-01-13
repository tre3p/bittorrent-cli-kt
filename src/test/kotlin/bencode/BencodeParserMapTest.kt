package bencode

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class BencodeParserMapTest {

    @Test
    fun shouldCorrectlyParseBencodedMapFromString() {
        val bencodedMap = "d5:helloi12ei13ei99e4:listl13:somethinghereee"
        val expectedMap = mapOf("hello" to 12, 13 to 99, "list" to listOf("somethinghere"))
        val actualMap = parseBencode(bencodedMap)[0]

        assertEquals(expectedMap, actualMap)
    }

    @Test
    fun shouldCorrectlyParseBencodedMapFromByteArray() {
        val bencodedMap = "d4:testi10ee".toByteArray()
        val expectedMap = mapOf("test" to 10)
        val actualMap = parseBencode(bencodedMap)[0]

        assertEquals(expectedMap, actualMap)
    }

    @Test
    fun shouldThrowExceptionOnAmountOfKeysIsBiggerThanValues() {
        val bencodedMap = "d5:helloi13e3:abc4:listl13:somethinghereee"
        assertThrows<BencodeParseException> { parseBencode(bencodedMap) }
    }
}
