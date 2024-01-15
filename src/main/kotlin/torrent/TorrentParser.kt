package torrent

import bencode.decodeBencode
import bencode.encodeToBencode
import java.io.File
import java.security.MessageDigest

data class Torrent(
    val announce: String,
    val info: Map<String, Any>
)

fun parseTorrentFile(torrentFile: File): Torrent {
    val fileBytes = torrentFile.readBytes()
    val decodedBencode = decodeBencode(fileBytes)[0] as Map<String, Any>

    val announceUrl = String(decodedBencode["announce"] as ByteArray)
    val infoMap = decodedBencode["info"] as Map<String, Any>

    return Torrent(announceUrl, infoMap)
}

@OptIn(ExperimentalStdlibApi::class) // TODO: remove it when toHexString() is no longer experimental API
fun calculateTorrentInfoHash(torrent: Torrent): String {
    val bencodedInfoMap = encodeToBencode(torrent.info)
    val infoHashHex = MessageDigest.getInstance("SHA-1").digest(bencodedInfoMap).toHexString()

    return infoHashHex
}