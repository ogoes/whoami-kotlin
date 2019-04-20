

class Ele (val i: String) {

  fun get (): String {
    return i
  }


}

fun main(args: Array<String>) {


  var list = mutableListOf <Ele> (Ele("0"), Ele("1"), Ele("2"))

  println(list.binarySearchBy ("0") { it.get() })
}
