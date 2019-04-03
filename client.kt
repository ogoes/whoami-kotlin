
import java.net.Socket

fun main(args: Array<String>) {
  val client = Socket("localhost", 9999)
  client.use {
    it.outputStream.write("hello socket world".toByteArray())
  }
}
