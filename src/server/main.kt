package server

import server.entities.Server

fun main(args: Array<String>) {
  val server: Server = Server("localhost", 9999)

  server.startServer()
}
