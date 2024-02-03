package torrent

import bencode.decodeBencode
import bencode.encodeToBencode
import client.discoverPeers
import dto.TorrentClientInfo
import entity.Peer
import kotlinx.coroutines.runBlocking
import java.io.File
import java.lang.Exception
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

fun calculateTorrentInfoHash(torrent: Torrent): ByteArray {
    val bencodedInfoMap = encodeToBencode(torrent.info)
    return MessageDigest.getInstance("SHA-1").digest(bencodedInfoMap)
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

@OptIn(ExperimentalUnsignedTypes::class)
fun decodePeers(peersBytes: ByteArray): List<Peer> {
    val unsignedPeersBytes = peersBytes.toUByteArray().toByteArray()

    return unsignedPeersBytes.toList().chunked(6)
        .stream()
        .map {
            val peerIp = "${it.slice(0..3).joinToString(".")}"
            val peerPort = ByteBuffer.wrap(it.slice(4..5).toByteArray()).order(ByteOrder.BIG_ENDIAN).getShort().toUShort()

            Peer("$peerIp:$peerPort")
        }
        .collect(Collectors.toList())
}

@OptIn(ExperimentalStdlibApi::class)
fun main() {
    val f = parseTorrentFile(File("debian-arm.torrent"))
    val torrentInfoHash = calculateTorrentInfoHash(f)
    val torrentClientInfo = TorrentClientInfo(
        torrentInfoHash.toHexString(),
        "00112233445566778899",
        6889,
        0,
        0,
        0,
        1
    )
    val discoverPeers = runBlocking { discoverPeers(f.announce, torrentClientInfo) }
    val map = decodeBencode(discoverPeers)[0] as Map<String, Any>
    val peers = decodePeers(map["peers"] as ByteArray)

    for (peer in peers) {
        val payload = byteArrayOf(19)
            .plus("BitTorrent protocol".toByteArray())
            .plus(ByteArray(8))
            .plus(torrentInfoHash)
            .plus("00112233445566778899".toByteArray())

        peer.connect { outStream, inStream ->
            val resp = ByteArray(payload.size)
            try {
                outStream.write(payload)
                inStream.readFully(resp)
            } catch (e: Exception) {
                println("Error while handshaking peer")
            }

            println("Peer ID: ${resp.takeLast(20).toByteArray().toHexString()}") // Use only the last 20 bytes.
        }
    }
}