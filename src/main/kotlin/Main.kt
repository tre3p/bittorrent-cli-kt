import client.discoverPeers
import dto.TorrentClientInfo
import entity.PeerPool
import entity.Torrent
import kotlinx.coroutines.*
import java.io.File
import kotlin.text.toHexString

private const val CLIENT_ID = "00112233445566778899"
private const val CLIENT_PORT = 6889

@OptIn(ExperimentalStdlibApi::class)
fun main() = runBlocking {
    val f = Torrent(File("debian.torrent"))
    val torrentInfoHash = f.getInfoHash()
    val torrentClientInfo = TorrentClientInfo(
        torrentInfoHash.toHexString(),
        CLIENT_ID,
        CLIENT_PORT,
        0,
        0,
        0,
        1
    )

    val peers = discoverPeers(f.announceUrl, torrentClientInfo)
    val alivePeerPool = PeerPool()

    val handshakeJobs = mutableListOf<Job>()
    for (peer in peers) {
        handshakeJobs.add(launch {
            withContext(Dispatchers.IO) {
                if (peer.handshake(torrentInfoHash, CLIENT_ID.toByteArray())) {
                    alivePeerPool.pool.addLast(peer)
                }
            }
        })
    }

    handshakeJobs.joinAll()
    addClosePeerConnectionsShutdownHook(alivePeerPool)

    println("Alive peers count: ${alivePeerPool.pool.size}/${peers.size}")
}

fun addClosePeerConnectionsShutdownHook(peerPool: PeerPool) {
    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            println("Closing peer connections")
            peerPool.pool.forEach { it.close() }
            println("Peer connections are gracefully closed")
        }
    })
}