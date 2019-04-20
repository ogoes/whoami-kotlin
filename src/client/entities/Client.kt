package client.entities

import interfaces.IO

import java.net.Socket
import java.net.SocketException
import java.nio.charset.Charset
import java.io.InputStream
import java.io.OutputStream
import java.util.*


class Client (): IO   {

  private lateinit var _serverSocket: Socket
  private lateinit var _clientSocket: Socket
  private var _isMaster: Boolean = false
  private var _score: Int = 0
  private var _username: String = "default"



  fun getInputClient (): InputStream {
    return _clientSocket.getInputStream()
  }

  fun getOutputClient (): OutputStream {
    return _clientSocket.getOutputStream()
  }

  fun getClientSocket (): Socket {
    return _clientSocket
  }
  fun setClientSocket (client: Socket) {
    _clientSocket = client
  }
  fun getServerSocket (): Socket {
    return _serverSocket
  }
  fun setServerSocket (server: Socket) {
    _serverSocket = server
  }
  fun unsetMaster () {
    _isMaster = false
  }
  fun setMaster () {
    _isMaster = true
  }
  fun getScore (): Int {
    return _score
  }
  fun decScore () {
    _score -= 1
  }
  fun incScore () {
    _score += 1
  }
  fun setUsername (user: String) {
    _username = user
  }
  fun getUsername (): String {
    return _username
  }
  fun connectServer (serverIP: String, serverPort: Int): Int {

    try {
      val connection: Socket = Socket(serverIP, serverPort)
      
      connectionInitialize(connection)
    } catch (e: SocketException) {
      println("\nHouveram erros na conexão com o servidor.\n\nVerifique o endereço ou a porta especificada\n\n\tEndereço IP: ${serverIP}\n\tPorta: ${serverPort}")
      return 1
    }

    return 0
  }

  fun connectionInitialize (server: Socket) {
    val reader = server.getInputStream()
    val writer = server.getOutputStream()

    var username: String

    sendMessage(writer, "INIT")


    var message = receiveMessage(reader)
    while (message != "END" ) {
      while (message == "ASK" || message == "ERROR1") {
        print("\\> ")
        username = readLine()!!
        sendMessage(writer, "ANSWER")
        if (receiveMessage(reader) == "OK") {
          sendMessage(writer, username)
          message = receiveMessage(reader)        
        }
        if (message == "ERROR1") {
          println("\nERRO: nome atualmente em uso.\nNovo nome:")
        } else if (message == "OK") {
          println("\n[$username] entrou no jogo\n")
          println("Aguardando o início da partida.............")

          play(server)
        }

      }
      if (!noWhiteSpaces(message) && !upperCase(message)) {
        print(message)
      }
      sendMessage(writer, "GOTIT")
      message = receiveMessage(reader)
    }
    sendMessage(writer, "ENDED")
    println("sdasd")
  }

  fun play (serverSocket: Socket) {
    _serverSocket = serverSocket
    val reader = _serverSocket.getInputStream()
    val writer = _serverSocket.getOutputStream()

    sendMessage(writer, "OK")
    var message = receiveMessage(reader)
    while (message != "END") {
      if (!noWhiteSpaces(message) && !upperCase(message)) {
        print(message)
      }
      sendMessage(writer, "GOTIT")
      message = receiveMessage(reader)
    }
    sendMessage(writer, "OK ")

  }
}

