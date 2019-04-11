package client.entities

import interfaces.IO

import java.net.Socket
import java.net.SocketException
import java.nio.charset.Charset
import java.util.*


class Client (val serverIP: String, val serverPort: Int): IO {

  fun connectServer (): Int {

    try {
      val connection: Socket = Socket(serverIP, serverPort)
      
      val message = receiveMessage(connection.getInputStream())
      print(message)

      val username = readLine()!!

      sendMessage(connection.getOutputStream(), username)
    } catch (e: SocketException) {
      println("\nHouveram erros na conexão com o servidor.\n\nVerifique o endereço ou a porta especificada\n\n\tEndereço IP: ${serverIP}\n\tPorta: ${serverPort}")
      return 1
    }

    return 0
  }
}

