import java.io.File
import java.util.*


fun main() {
    BOOST("src/input/day9-input.txt", 1L)
    BOOST("src/input/day9-input.txt", 2L)
}

//run(instructions: MutableList<Long>, input: Queue<Long>, initialPointer: Int = 0)
fun BOOST(file: String, inputParam: Long = 1) : Long {
    val instructions = File(file).readText().trim().split(",").map { num -> num.toLong() }.toMutableList()
    instructions.addAll(MutableList(10000){0L})
    val input = ArrayDeque<Long>()
    input.add(inputParam)
    val result = run(instructions, input)
    println(result)
    return result.output
}