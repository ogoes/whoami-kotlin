package server.entities

import interfaces.IO

import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.util.*

class Server (val ip: String, val port: Int): IO {
  

  fun startServer () {
    val serverSocket: ServerSocket

    serverSocket = ServerSocket(port)
    println("Server running in ${ip}:${port}")

    while (true) {
      var client = serverSocket.accept()

      val handlerThread = Thread {
        clientHandler(client)
      }
      handlerThread.start()

    }
  }


  fun clientHandler (client: Socket) {

    sendMessage(client.getOutputStream(), "Informe o seu username: ")

    val username = receiveMessage(client.getInputStream())

    println(username)
  }
}
