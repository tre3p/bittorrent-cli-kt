package bencode.decoder

import bencode.decodeBencode
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BencodeDecoderListTest {

    @Test
    fun shouldCorrectlyParseList() {
        val bencodeString = "l11:bencodetesti42e4:hihie"
        val expectedElements = listOf("bencodetest", 42, "hihi")

        val actualList = decodeBencode(bencodeString)[0]
        assertEquals(expectedElements, actualList)
    }

    @Test
    fun shouldCorrectlyParseListFromByteArray() {
        val bencodedList = "l11:bencodetesti42ee".encodeToByteArray()
        val expectedList = listOf("bencodetest", 42)
        val actualList = decodeBencode(bencodedList)[0]

        assertEquals(expectedList, actualList)
    }

    @Test
    fun shouldCorrectlyParseListWithNestedList() {
        val bencodeString = "l5:helloli42e2:hiee"
        val expectedElements = listOf("hello", listOf(42, "hi"))

        val actualList = decodeBencode(bencodeString)[0]
        assertEquals(expectedElements, actualList)
    }
}
