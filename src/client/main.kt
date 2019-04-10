package client


import client.entities.Client

fun main(args: Array<String>) {
  val client: Client = Client("localhost", 9999)

  client.connectServer()

}
