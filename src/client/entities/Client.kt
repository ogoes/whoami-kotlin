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
  private var _hit: Boolean = false



  fun unsetHit () {
    _hit = false
  }

  fun setHit () {
    incScore()
    _hit = true
  }

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

    var username: String = ""
    var ANSWER: String = "GOTIT"



    var message = receiveMessage(reader)
    while (message != "END") {
      if (message == "ASK" || message == "ERROR1") {
        if (message == "ERROR1") {
          println("\nERRO: nome atualmente em uso.\nNovo nome:")
        }
        print("\\> ")
        username = readLine()!!
        ANSWER = username
      }
      if (!noWhiteSpaces(message) && !upperCase(message)) {
        print(message)
        ANSWER = "GOTIT"
      }
      sendMessage(writer, ANSWER)
      message = receiveMessage(reader)
    }

    setUsername(username)
    println("\n[$username] entrou no jogo\n")
    println("Aguardando o início da partida.............") 
    sendMessage(writer, "ENDED")
    showPlayers(server)

  }


  fun showPlayers (serverSocket: Socket) {
    _serverSocket = serverSocket
    val reader = _serverSocket.getInputStream()
    val writer = _serverSocket.getOutputStream()


    var message = receiveMessage(reader)
    sendMessage(writer, "OK")
    
    while (message != "END") {
      if (!noWhiteSpaces(message) && !upperCase(message)) {
        print(message)
      }
      message = receiveMessage(reader)
      sendMessage(writer, "GOTIT")
    }

    play()
  }

  fun play () {

    val reader = _serverSocket.getInputStream()
    val writer = _serverSocket.getOutputStream()

    var message = receiveMessage(reader)

    if (message == "MASTER") {
      defineRules();
    }

    sendMessage(writer, "OK")
    getInstructions()



    while (true) {
      message = receiveMessage(reader)

      if (message == "MASTER") {

      } else if (message == "YOU") {
        yourTurn();
      } else if (!noWhiteSpaces(message) && !upperCase(message)) {
        print(message)
      }

      sendMessage(writer, "OK")

    }

  }

  fun defineRules () {

    val reader = _serverSocket.getInputStream()
    val writer = _serverSocket.getOutputStream()

    setMaster()
    sendMessage(writer, "OK")
    var message = receiveMessage(reader)
    var ANSWER = "GOTIT"

    while (message != "END") {
      if (!noWhiteSpaces(message) && !upperCase(message)) {
        print(message)
        ANSWER = "GOTIT"
      }
      if (message == "ASKD" || message == "ASKR") {
        print("\n\\> ")
        ANSWER = readLine()!!
      }
      sendMessage(writer, ANSWER)
      message = receiveMessage(reader)
    }
  }

  fun getInstructions () {
    val reader = _serverSocket.getInputStream()
    val writer = _serverSocket.getOutputStream()

    var message = receiveMessage(reader)
    sendMessage(writer, "OK")

    while (message != "END") {
      if (!noWhiteSpaces(message) && !upperCase(message)) {
        print(message)
      }
      message = receiveMessage(reader)
      sendMessage(writer, "GOTIT")
    }
  }

  fun yourTurn () {
    val reader = _serverSocket.getInputStream()
    val writer = _serverSocket.getOutputStream()

    sendMessage(writer, "OK")
    var message = receiveMessage(reader)
    var ANSWER = "GOTIT"

    while (message != "ASKP" && message != "ASKT") {
      if (!noWhiteSpaces(message) && !upperCase(message)) {
        print(message)
      }
      sendMessage(writer, "GOTIT")
      message = receiveMessage(reader)
    }

    print("\n\\> ")
    sendMessage(writer, readLine()!!)
    receiveMessage(reader)
  }

  fun masterAnswer () {
    val reader = _serverSocket.getInputStream()
    val writer = _serverSocket.getOutputStream()

    sendMessage(writer, "OK")
    var message = receiveMessage(reader)
    var ANSWER = "GOTIT"

    while (message != "END") {
      if (!noWhiteSpaces(message) && !upperCase(message)) {
        print(message)
        ANSWER = "GOTIT"
      }
      if (message == "ASK" || message == "ERRO") {
        if (message == "ERRO") {
          print("Resposta errada")
        }
        print("\n\\> ")
        ANSWER = readLine()!!
      }
      sendMessage(writer, ANSWER)
      message = receiveMessage(reader)
    }
    sendMessage(writer, "ENDED")
  }
}

