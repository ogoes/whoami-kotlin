

import java.io.InputStream
import java.net.ServerSocket
import java.net.Socket


fun main(args: Array<String>) {
  val Server = ServerSocket(9999)
  println("server rodando na porta ${Server.localPort}")


  while (true) {
    val client = Server.accept()
    println("Cliente Conectado ${client.inetAddress.hostAddress}")

  }
}
