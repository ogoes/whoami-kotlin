package server.entities

import interfaces.IO

import client.entities.Client

import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.util.*

class Server (val ip: String, val port: Int): IO {
  private lateinit var _master: Client
  private var _clientList: MutableList <Client> = ArrayList()
  private var _allowConnection: Boolean = true
  private var _timer: Int = 60
  private var _clientNumber: Int = 3

  fun startServer () {
    val serverSocket: ServerSocket

    serverSocket = ServerSocket(port)
    println("Server running in ${ip}:${port}")

    while (_allowConnection) {
      var client = serverSocket.accept()
      if (_allowConnection) {
        val handlerThread = Thread {
          clientHandler(client)
        }
        handlerThread.start()
      }
    }

    initPlay()

  }

  fun findClientByUsername (username: String): Int {
    var client = _clientList.find { it.getUsername() == username }

    if (client == null) return -1
    else return _clientList.indexOf(client)
  }

  fun clientHandler (client: Socket) { // when a new client request connection

    val clientWriter = client.getOutputStream() // send message to new client
    val clientReader = client.getInputStream() // receive message from new client


    val messages = listOf <String> (
      "BEGIN",
      "----------------------------------------------------------------\n",
      "BEM VINDO AO \"Who Am I\"\n",
      "----------------------------------------------------------------\n",
      "Atualmente há ${_clientList.size} jogadores conectados\n\n",
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
          if (findClientByUsername(username) == -1) {
            sendMessage(clientWriter, "OK")
            addClient(username, client)
          } else {
            sendMessage(clientWriter, "ERROR1")
          }
          message = receiveMessage(clientReader)
        }
      }
    }
    println(_clientList.map { it -> it.getUsername() })
  }

  fun addClient (username: String, clientSocket: Socket) {
    var client = Client()
    client.setUsername(username)
    client.setClientSocket(clientSocket)

    if (_clientList.size == 0) {
      client.setMaster()
      _master = client
    }

    _clientList.add(client)
    if (_clientList.size == _clientNumber) {
      _allowConnection = false
      var finish = Socket(ip, port)
      finish.close()
    }
  }
  
  fun initPlay () {
    val messages = listOf <String> (
      "BEGIN",
      "\n\n\n\n----------------------------------------------------------------\n",
      "INICIANDO PARTIDA\n",
      "----------------------------------------------------------------\n",
      "Jogadores conectados: ${_clientList.size}\n",
      "PLAYERS",
      "MESTRE da rodada: [${_master.getUsername()}]\n",
      "Aguardando definição de dica e resposta pelo MESTRE...",
      "END"
    )

    var message: String
    _clientList.forEach {
      for (msg in messages) {
        if (msg == "PLAYERS") {
          for (i in _clientList) {
            sendMessage(it.getOutputClient(), "\t[${i.getUsername()}]\n")
            message = receiveMessage(it.getInputClient())        
          }
        } else {
          sendMessage(it.getOutputClient(), msg)
          message = receiveMessage(it.getInputClient())
        }
      }
    }

    
  }
  
}
