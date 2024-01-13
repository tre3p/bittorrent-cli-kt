package bencode

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BencodeParserListTest {

    @Test
    fun shouldCorrectlyParseList() {
        val bencodeString = "l11:bencodetesti42e4:hihie"
        val expectedElements = listOf("bencodetest", 42, "hihi")

        val actualList = parseBencode(bencodeString)[0]
        assertEquals(expectedElements, actualList)
    }

    @Test
    fun shouldCorrectlyParseListWithNestedList() {
        val bencodeString = "l5:helloli42e2:hiee"
        val expectedElements = listOf("hello", listOf(42, "hi"))

        val actualList = parseBencode(bencodeString)[0]
        assertEquals(expectedElements, actualList)
    }
}
