package server.entities

import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.util.*

class Server (val ip: String, val port: Int) {
  

  fun startServer () {
    val serverSocket: ServerSocket

    serverSocket = ServerSocket(port)
    println("Server running in ${ip}:${port}")

    while (true) {
      var client = serverSocket.accept()

      clientHandler(client)
    }
  }


  fun clientHandler (client: Socket) {
    var byteArraySize = ByteArray(1024)
    client.getInputStream.read(byteArraySize)
  }
}
