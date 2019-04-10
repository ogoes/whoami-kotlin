package client.entities

import interfaces.IO

import java.net.Socket
import java.nio.charset.Charset
import java.util.*


class Client (val serverIP: String, val serverPort: Int): IO {

  fun connectServer () {
    val connection: Socket = Socket(serverIP, serverPort)
    
    val message = receiveMessage(connection.getInputStream())
    print(message)

    val username = readLine()!!

    sendMessage(connection.getOutputStream(), username)
  }
}

