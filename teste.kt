

class Ele (val i: String) {

  fun get (): String {
    return i
  }


}

fun main(args: Array<String>) {


  // var list = mutableListOf <Ele> (Ele("ogoes"), Ele("ada"), Ele("ele"))

  // for (e in list) {
  //   println(e.get())
  // }

  var rx = Regex("n[aã]o", RegexOption.IGNORE_CASE).matches("NÃi")
  println(rx)
  // println(Regex("[\\w]+").find(rx.find("MESTRE [ogoes]")!!.value)!!.value)

}
