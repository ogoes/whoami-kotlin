package client


import client.entities.Client

fun main(args: Array<String>) {
  var serverIP: String
  var serverPort: Int

  try {
    val argSplited = args[0].split(":")

    serverIP = argSplited[0]
    serverPort = argSplited[1].toInt()
    

    val client: Client = Client()
    client.connectServer(serverIP, serverPort)

  } catch (e: IndexOutOfBoundsException) {
    if (args.size == 0) {
      println("Você deve informar o endereço e a porta ==> endereço:porta")
    } else if (args[0].split(":").size == 1) {
      println("Você deve informar o endereço e a porta ==> endereço:porta")
    }
  } catch (e: NumberFormatException) {
    println("Porta errada, deve ser um numero inteiro")
  }


}
