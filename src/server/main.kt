package server

import server.entities.Server

fun main(args: Array<String>) {
  var serverIP: String = "localhost"
  var serverPort: Int = 9999

  if (args.size >= 1) {
    serverIP = args[0].split(":")[0] 
    serverPort = args[0].split(":")[1].toInt() 

  }

  val server: Server = Server(serverIP, serverPort)
  server.startServer()
}
