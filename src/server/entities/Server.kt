package server.entities

import interfaces.IO

import client.entities.Client

import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.util.*

class Server (val ip: String, val port: Int): IO {
  private lateinit var _master: Client
  private var _clientList: MutableList<Client> = ArrayList()
  private var _allowConnection: Boolean = true
  private var _timer: Int = 60

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

  fun findClientByUsername (username: String): Int {
    return _clientList.binarySearchBy (username) { it.getUsername() }
  }
  fun clientHandler (client: Socket) { // when a new client request connection

    val clientWriter = client.getOutputStream() // send message to new client
    val clientReader = client.getInputStream() // receive message from new client


    val messages = listOf <String> (
      "BEGIN",
      "----------------------------------------------------------------\n",
      "BEM VINDO AO \"Who Am I\"\n",
      "----------------------------------------------------------------\n",
      "Atualmente há ${_clientList.size} jogadores conectados\n",
      "Próxima partida inicia em : 30 segundos\n\n",
      "Para começar, nos diga: Quem é você?\n",
      "ASK",
      "END"
    )


    var message = receiveMessage(clientReader)

    if (message == "INIT") {
      messages.forEach {
        sendMessage(clientWriter, it)
        message = receiveMessage(clientReader)
        while (message == "ANSWER") {
          sendMessage(clientWriter, "OK")
          var username = receiveMessage(clientReader)
          if (findClientByUsername(username) < 0) {
            sendMessage(clientWriter, "OK")
            println("COMING SOON")
          } else {
            sendMessage(clientWriter, "ERROR1")
          }
          message = receiveMessage(clientReader)
        }
      }
    } 
  }

  
}
