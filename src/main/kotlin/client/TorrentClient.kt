package client

import bencode.decodeBencode
import dto.TorrentClientInfo
import entity.Peer
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import java.util.stream.Collectors

private val httpClient = HttpClient(CIO)

suspend fun discoverPeers(discoverPeersUrl: String, torrentClientInfo: TorrentClientInfo): List<Peer> {
    val resp = httpClient.get(discoverPeersUrl) {
        url {
            encodedParameters.append("info_hash", urlEncodeInfoHash(torrentClientInfo.infoHash))
            parameters.append("peer_id", torrentClientInfo.peerId)
            parameters.append("port", torrentClientInfo.port.toString())
            parameters.append("uploaded", torrentClientInfo.uploaded.toString())
            parameters.append("downloaded", torrentClientInfo.downloaded.toString())
            parameters.append("left", torrentClientInfo.left.toString())
            parameters.append("compact", torrentClientInfo.compact.toString())
        }
    }

    val announceResponse = resp.body<ByteArray>()

    val announceResponseMap = decodeBencode(announceResponse).first() as Map<String, Any>
    val peersBytes = (announceResponseMap["peers"] as ByteArray)
    return peersBytes.toList().chunked(6)
        .stream()
        .map { Peer(it.toByteArray()) }
        .collect(Collectors.toList())
}

@OptIn(ExperimentalStdlibApi::class)
private fun urlEncodeInfoHash(infoHash: ByteArray): String {
    return infoHash.toHexString().chunked(2)
        .joinToString(separator = "%", prefix = "%")
}