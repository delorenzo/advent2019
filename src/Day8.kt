import java.io.File

const val BLACK = 0
const val WHITE = 1
const val TRANSPARENT = 2

fun main() {
    val wide = 25
    val tall = 6
    val total = wide*tall
    val layers = layers("src/input/day8-input.txt", wide, tall)
    println(fewest(layers))
    val newMessage = decode(layers, total)
    for (i in 0 until total step wide) {
        println(newMessage.subList(i, i+wide).toString())
    }
}

fun layers(file: String, wide: Int, tall: Int) : MutableList<List<Int>> {
    val ints = File(file).readText().trim().map { it.toString().toInt() }
    val layers = mutableListOf<List<Int>>()
    val total = wide*tall
    for (i in ints.indices step total) {
        layers.add(ints.subList(i, i+total))
    }
    return layers
}

fun fewest(layers:  List<List<Int>>) : Int {
    val sortedLayers = layers.sortedBy { layer -> layer.count { num -> num == 0 } }
    val fewest = sortedLayers.first()
    val ones = fewest.count { it == 1 }
    val twos = fewest.count { it == 2 }
    return ones * twos
}

fun decode(layers: MutableList<List<Int>>, total: Int) : List<Int> {
    val newMessage = MutableList(total) { TRANSPARENT }
    for (i in 0 until total) {
        val slice = layers.getSlice(i)
        val firstWhite = slice.indexOfFirst { it == WHITE }
        val firstBlack = slice.indexOfFirst { it == BLACK }
        newMessage[i] = when {
            firstWhite >= 0 && firstBlack < 0 -> WHITE
            firstBlack >= 0 && firstWhite < 0 -> BLACK
            firstWhite < firstBlack -> WHITE
            firstBlack < firstWhite -> BLACK
            else -> TRANSPARENT
        }
    }
    return newMessage
}

fun MutableList<List<Int>>.getSlice(pos: Int) : List<Int> {
    return this.map { it[pos] }
}