package client

import dto.TorrentClientInfo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*

private val httpClient = HttpClient(CIO)

suspend fun discoverPeers(discoverPeersUrl: String, torrentClientInfo: TorrentClientInfo): ByteArray {
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

    return resp.body<ByteArray>()
}

private fun urlEncodeInfoHash(infoHash: String): String {
    return infoHash.chunked(2)
        .joinToString(separator = "%", prefix = "%")
}