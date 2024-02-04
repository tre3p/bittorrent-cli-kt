package entity

import java.io.DataInputStream
import java.lang.Exception
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder.BIG_ENDIAN
import javax.net.SocketFactory

class Peer(private val socketAddress: InetSocketAddress) {

    private var peerId: String? = null
    private var peerConnection: Socket? = null

    companion object PeerConstants {
        private val PEER_IP_BYTES_RANGE = IntRange(0, 3)
        private val PEER_PORT_BYTES_RANGE = IntRange(4, 5)

        private const val PEER_CONNECTION_TIMEOUT_MS = 5000
        private const val BITTORRENT_PROTOCOL_ID: Byte = 19
        private val BITTORRENT_PROTOCOL_STR = "BitTorrent protocol".toByteArray()
        private const val RESERVED_BYTES_AMOUNT = 8
    }

    constructor(socketAddress: ByteArray) : this(
        InetSocketAddress(
            InetAddress.getByAddress(socketAddress.sliceArray(PEER_IP_BYTES_RANGE)),
            ByteBuffer.wrap(socketAddress.sliceArray(PEER_PORT_BYTES_RANGE)).order(BIG_ENDIAN).getShort().toInt() and 0xFFFF
        )
    )

    @OptIn(ExperimentalStdlibApi::class)
    fun handshake(infoHash: ByteArray, torrentClientId: ByteArray): Boolean {
        if (this.peerConnection == null) {
            try {
                connect()
            } catch (e: Exception) {
                // TODO: log peer connect exception
                return false
            }
        }

        val handshakePayload = byteArrayOf(BITTORRENT_PROTOCOL_ID)
            .plus(BITTORRENT_PROTOCOL_STR)
            .plus(ByteArray(RESERVED_BYTES_AMOUNT))
            .plus(infoHash)
            .plus(torrentClientId)

        try {
            val peerConnectionOutStream = this.peerConnection!!.getOutputStream()
            val peerConnectionInStream = DataInputStream(this.peerConnection!!.getInputStream())

            peerConnectionOutStream?.write(handshakePayload)
            val peerResponse = ByteArray(handshakePayload.size)

            peerConnectionInStream.readFully(peerResponse)
            this.peerId = peerResponse.takeLast(20).toByteArray().toHexString()
        } catch (e: Throwable) {
            // TODO: log hanshake exception
            return false
        }

        println("Handshake successfully! ID: ${this.peerId}, IP: ${this.socketAddress.hostString}, port: ${this.socketAddress.port}")
        return true
    }

    private fun connect() {
        val clientSocket = SocketFactory.getDefault().createSocket()
        clientSocket.connect(this.socketAddress, PEER_CONNECTION_TIMEOUT_MS)
        this.peerConnection = clientSocket
    }

    fun close() {
        this.peerConnection?.close()
    }
}