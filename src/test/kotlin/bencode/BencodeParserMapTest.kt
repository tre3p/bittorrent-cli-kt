package bencode

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class BencodeParserMapTest {

    @Test
    fun shouldCorrectlyParseBencodedMap() {
        val bencodedMap = "d5:helloi12ei13e3:abc4:listl13:somethinghereee"
        val expectedMap = mapOf("hello" to 12, 13 to "abc", "list" to listOf("somethinghere"))
        val actualMap = parseBencode(bencodedMap)[0].first

        assertEquals(expectedMap, actualMap)
    }

    @Test
    fun shouldThrowExceptionOnAmountOfKeysIsBiggerThanValues() {
        val bencodedMap = "d5:helloi13e3:abc4:listl13:somethinghereee"
        assertThrows<BencodeParseException> { parseBencode(bencodedMap) }
    }
}
