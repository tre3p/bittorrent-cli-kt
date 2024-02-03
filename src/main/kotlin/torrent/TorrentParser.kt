package torrent

import bencode.decodeBencode
import bencode.encodeToBencode
import client.discoverPeers
import dto.TorrentClientInfo
import kotlinx.coroutines.runBlocking
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.MessageDigest
import java.util.Arrays
import java.util.stream.Collectors
import kotlin.text.toHexString

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

@OptIn(ExperimentalStdlibApi::class) // TODO: remove it when toHexString() is no longer experimental API
fun getPieceHashes(torrent: Torrent): List<String> {
    return (torrent.info["pieces"] as ByteArray)
        .toList()
        .chunked(20) // amount of bytes in one piece hash
        .stream()
        .map { it.toByteArray().toHexString() }
        .collect(Collectors.toList())
}

fun main() {
    val f = parseTorrentFile(File("debian.torrent"))
    val infoHash = calculateTorrentInfoHash(f)
    val torrentClientInfo = TorrentClientInfo(
        urlEncodeInfoHash(infoHash),
        "00112233445566778899",
        6881,
        0,
        0,
        0,
        1
    )

    val discoverPeersResponse = runBlocking { discoverPeers(f.announce, torrentClientInfo) }
    println(String(discoverPeersResponse))
    val decodeBencode = decodeBencode(discoverPeersResponse)[0] as Map<String, Any>
    println(decodeBencode)
    val p = (decodeBencode["peers"] as ByteArray).map { it.toUByte() }
    println(decodePeers(p))
}

fun urlEncodeInfoHash(infoHash: String): String {
    return infoHash.chunked(2)
        .joinToString(separator = "%", prefix = "%")
}

@OptIn(ExperimentalUnsignedTypes::class)
fun decodePeers(peersBytes: List<UByte>): List<String> {
    return peersBytes.toList().chunked(6)
        .stream()
        .map {
            val peerIp = "${it.slice(0..3).joinToString(".")}"
            val peerPort = ByteBuffer.wrap(it.slice(4..5).toUByteArray().toByteArray()).order(ByteOrder.BIG_ENDIAN).getShort().toUShort()

            "$peerIp:$peerPort"
        }
        .collect(Collectors.toList())
}