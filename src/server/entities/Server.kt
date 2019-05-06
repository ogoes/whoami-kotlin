package server.entities

import interfaces.IO

import client.entities.Client

import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.util.*

class Server (val ip: String, val port: Int): IO {
  private lateinit var _master: Client
  private lateinit var _player: Client
  private var _clientList: MutableList <Client> = ArrayList()
  private var _clientNumber: Int = 2
  private var _tip: String = ""
  private var _answer: String = ""
  private var _curMasterIndex: Int = 0
  private var _curPlayerIndex: Int = 1
  private var _timer: Int = 60
  private var _allowConnection: Boolean = true
  private var _inGame: Boolean = true

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

    sendPlayers()
    defineRules()
    sendInstruction()

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
      "Para começar, nos diga: Quem é você?\n"
    )

    var message: String

    messages.forEach {
      sendMessage(clientWriter, it)
      message = receiveMessage(clientReader)
    }



    var username: String = "default"


    sendMessage(clientWriter, "ASK")
    message = receiveMessage(clientReader)
    while (message != "ENDED") {
      if (findClientByUsername(message) == -1) {
        username = message
        sendMessage(clientWriter, "END")
      } else {
        sendMessage(clientWriter, "ERROR1")
      }
      message = receiveMessage(clientReader)
    }

    addClient(username, client)
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
  
  fun sendPlayers () {
    var messages = listOf <String> (
      "BEGININIT",
      "\n\n\n\n----------------------------------------------------------------\n",
      "INICIANDO PARTIDA\n",
      "----------------------------------------------------------------\n",
      "Jogadores conectados: ${_clientList.size}\n",
      "PLAYERS",
      "MESTRE da rodada: [${_master.getUsername()}]\n",
      "Aguardando definição de dica e resposta pelo MESTRE...\n",
      "END"
    )

    var clients: String = ""
    for (i in _clientList) {
      clients += "\t[${i.getUsername()}]\n"
    }

    sendMessageForAll(messages.map { it -> if (it == "PLAYERS") clients else it })
  }

  fun defineRules () {
    val messages = listOf <String> (
      "BEGINRULES",
      "\n\n\n\n----------------------------------------------------------------\n",
      "MESTRE DA RODADA\n",
      "----------------------------------------------------------------\n",
      "Informe a dica:",
      "ASKD",
      "Informe a resposta:",
      "ASKR",
      "END"
    )

    val reader = _master.getInputClient()
    val writer = _master.getOutputClient()

    sendMessage(writer, "MASTER");
    var message = receiveMessage(reader);
    if (message == "OK") {
      for (msg in messages) {
        sendMessage(writer, msg)
        message = receiveMessage(reader)
        if (msg == "ASKD") {
          _tip = message
        } else if (msg == "ASKR") {
          _answer = message
        }
      }
    }
  }

  fun sendInstruction () {

    sendMessageForAll(listOf <String> (
      "BEGININIT",
      "\n\n\n\n----------------------------------------------------------------\n",
      "PARTIDA INICIADA\n",
      "----------------------------------------------------------------\n",
      "MESTRE: [${_master.getUsername()}]\n",
      "Dica: \"${_tip}\"\n\n",
      "Instruções:\n",
      "> somente são permotidas perguntas com resposta do tipo SIM/NÃO\n",
      "> perguntas inadequadas serão invalidadas pelo MESTRE\n",
      "> JOGADOR perde a vez se fizer pergunta inadequada\n\n",
      "END"
    ))
  }
  
  fun sendMessageForAll(messages: List <String>) {
    _clientList.forEach {
      for (msg in messages) {
        sendMessage(it.getOutputClient(), msg)
        receiveMessage(it.getInputClient())
      }
    }
  }

  fun initGame () {
    _player = _clientList[_curPlayerIndex]
    while (_inGame) {
      sendMessageForAll(listOf <String> (
        "BEGIN",
        "JOGADOR da vez: [${_player.getUsername()}]\n"
      ))
    }
  }

  fun asking () {
    val readerPlayer = _player.getInputClient()
    val writerPlayer = _player.getOutputClient()

    val readerMaster = _master.getInputClient()
    val writerMaster = _master.getOutputClient()

    var pergunta = listOf <String> (
      "YOU",
      "\n\nDICA: \"${_tip}\"\n",
      "Sua pergunta:\n",
      "ASKP",
      "END"
    )
    var tentativa = listOf <String> (
      "YOU",
      "Tentativa:\n",
      "ASKT",
      "END"
    )

    sendMessage(writerPlayer, "YOU")
    var message = receiveMessage(readerPlayer)

    var question: String
    var attempt: String

    pergunta.forEach {
      sendMessage(writerPlayer, it)
      message = receiveMessage(readerPlayer)
      if (it == "ASKP") {
        question = message
      }
    }
    
    sendMessageForAll(listOf <String> (
      "BEGIN",
      "[${_player.getUsername()}]: ${question}",
      "END"
    ))

    

    var answer = askingMaster(
      listOf <String> (
        "MASTER",
        "[0] SIM",
        "[1] NÃO",
        "[2] INVÁLIDA"
      ),
      listOf <Regex> (
        Regex("^0|(n[aã]o)$", RegexOption.IGNORE_CASE),
        Regex("^1|(sim)$", RegexOption.IGNORE_CASE),
        Regex("^2|(inv[aá]lida)|(invalid)$", RegexOption.IGNORE_CASE)
      )
    )

    sendMessageForAll(listOf <String> (
      "BEGIN",
      "[MESTRE]: ${answer}",
      "END"
    ))

    sendMessage(writerPlayer, "YOU")
    var message = receiveMessage(readerPlayer)

    tentativa.forEach {
      sendMessage(writerPlayer, it)
      message = receiveMessage(readerPlayer)
      if (it == "ASKP") {
        attempt = message
      }
    }

    answer = askingMaster(
      listOf <String> (
        "MASTER",
        "[0] ACERTOU",
        "[1] ERROU",
      ),
      listOf <Regex> (
        Regex("^0|(acertou)(hit)$", RegexOption.IGNORE_CASE),
        Regex("^1|(errou)$", RegexOption.IGNORE_CASE),
      )
    )



    
    

    
  }

  fun askingMaster (questions: List <String>, regexes: List <Regex>): String {
    val readerMaster = _master.getInputClient()
    val writerMaster = _master.getOutputClient()

    var answer = "NOP!!!!"
    questions.forEach {
      sendMessage(writerMaster, it)
      var message = receiveMessage(readerMaster)
    }
    sendMessage(writerMaster, "ASK")
    message = receiveMessage(readerMaster)
    while (message != "ENDED") {
      for (rgx in regexes) {
        if (rgx.matches(message)) {
          sendMessage(writerMaster, "END")
          answer =  rgx.find(message).value
        }
      }
      if (answer == "NOP!!!!") {
        sendMessage(writerMaster, "ERRO")              
      }
      message = receiveMessage(readerMaster)
    }

    return answer
  }

}
