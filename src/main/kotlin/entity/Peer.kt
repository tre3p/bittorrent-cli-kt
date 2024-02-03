package entity

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import javax.net.SocketFactory

class Peer(val socket: InetSocketAddress) {

    constructor(socketAddress: String) : this(
        socketAddress.split(":").let { (ip, port) ->
            InetSocketAddress(
                InetAddress.getByName(ip),
                port.toInt()
            )
        }
    )

    fun connect(f: (outStream: DataOutputStream, inStream: DataInputStream) -> Unit) {
        SocketFactory.getDefault().createSocket().use { socket ->
            socket.connect(this.socket)
            f.invoke(
                DataOutputStream(socket.getOutputStream()),
                DataInputStream(socket.getInputStream())
            )
        }
    }
}