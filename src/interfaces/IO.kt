package interfaces

import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset

interface IO {
  

  fun noWhiteSpaces (message: String): Boolean {
    return Regex("\\w+?[^\\s]\\w+?").matches(message)
  }

  fun upperCase (message: String): Boolean {
    return Regex("[A-Z]+").matches(message)
  }

  fun sendMessage (outputStream: OutputStream, message: String) {

    val bufferedMessage = message.toByteArray(Charsets.UTF_8)
    outputStream.write(bufferedMessage)
    outputStream.flush()

  } 

  fun receiveMessage (inputStream: InputStream): String {
    val buffer = ByteArray(4096)
    val size = inputStream.read(buffer)

    return buffer.copyOf(size).toString(Charsets.UTF_8)
  }
}
