package dto

data class TorrentClientInfo(
    val infoHash: String,
    val peerId: String,
    val port: Int,
    val uploaded: Int,
    val downloaded: Int,
    val left: Int,
    val compact: Int
)
