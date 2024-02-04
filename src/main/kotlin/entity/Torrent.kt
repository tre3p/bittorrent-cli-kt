package entity

import bencode.decodeBencode
import bencode.encodeToBencode
import java.io.File
import java.security.MessageDigest
import java.util.stream.Collectors

class Torrent(torrentFile: File) {

    val announceUrl: String
    private val info: Map<String, Any>

    init {
        val fileBytes = torrentFile.readBytes()
        val decodedBencode = decodeBencode(fileBytes)[0] as Map<String, Any>
        val announceUrl = String(decodedBencode[ANNOUNCE_MAP_KEY] as ByteArray)
        val infoMap = decodedBencode[INFO_MAP_KEY] as Map<String, Any>

        this.announceUrl = announceUrl
        this.info = infoMap
    }

    companion object TorrentConstants {
        private const val ANNOUNCE_MAP_KEY = "announce"
        private const val INFO_MAP_KEY = "info"
        private const val PIECES_MAP_KEY = "pieces"

        private const val BYTES_IN_ONE_HASH_PIECE = 20
    }

    fun getInfoHash(): ByteArray {
        val bencodedInfoMap = encodeToBencode(this.info)
        return MessageDigest.getInstance("SHA-1").digest(bencodedInfoMap)
    }

    @OptIn(ExperimentalStdlibApi::class) // TODO: remove it when toHexString() is no longer experimental API
    fun getPieceHashes(): List<String> {
        return (this.info[PIECES_MAP_KEY] as ByteArray)
            .toList()
            .chunked(BYTES_IN_ONE_HASH_PIECE)
            .stream()
            .map { it.toByteArray().toHexString() }
            .collect(Collectors.toList())
    }
}