import java.io.File
import kotlin.math.abs

val BASE = listOf(0, 1, 0, -1)
val PATTERNS = mutableMapOf<Int, List<Int>>()

fun main() {
    part1("src/input/day16-input.txt")
    part2("src/input/day16-input5.txt")
}

fun part1(input: String) {
    var instructions = File(input).readText().trim().toCharArray().map { num -> num.toString().toInt() }.toList()
    //part 1
    for (i in 0 until 100) {
        instructions = phase(instructions)
        //println(instructions)
    }
    println()
    println()
    println(instructions.take(8).toString())
}

fun intToString(a: Int, b: Int) :String {
    return "$a$b"
}

fun part2(input: String) : String {
    var instructions = File(input).readText().trim().toCharArray().map { num -> num.toString().toInt() }.toList()
    val offset = instructions.take(7).map { it.toString() }.reduce { a, b -> "$a$b" }.toInt()

    val bigInstructions = repeat(instructions, 10000)
    //part 1
//    var patternOffset = offset
//    var offsetIndex = offset % 10000*instructions.size
//    while (offsetIndex > 0) {
//        patternOffset--
//        offsetIndex--
//    }
    for (i in 0 until 2) {
        //instructions = phase(instructions, 10000*instructions.size, offset-7)
        val pattern = Array<Int>(bigInstructions.size) {0}
        instructions = phase2(bigInstructions, pattern)
        println(instructions)
    }
    //var realOffset = offset % instructions.size
//    val response = StringBuilder()
//    for (i in offset until offset+7) {
//        var index = i % instructions.size
//        response.append(instructions[index])
//    }
    println(instructions)
    return ""
}

fun repeat(instructions: List<Int>, times: Int) : List<Int> {
    var bigList = mutableListOf<Int>()
    for (i in 0 until times) {
        bigList.addAll(instructions)
    }
    return bigList
}

///10000*instructions.size
fun phase(instructions: List<Int>, size: Int, offset: Int) : List<Int> {
    val output = mutableListOf<Int>()
    for (i in instructions.indices) {
        var sum = 0L
        for (j in 0 until size) {
            sum += instructions[j % instructions.size] * getCurrentPattern(j, j)
        }
        var digit = sum.toString().takeLast(1).toInt()
        output.add(digit)
    }
    return output
}

val cache = mutableMapOf<Pair<Int, Int>, Int>()
fun getCurrentPattern(position: Int, index: Int) : Int {
    var multiple = 0
    if (position !=0)  {
         multiple = index / position
    }
    return BASE[(multiple + 1) % BASE.size]
}

//fun phase2(instructions: List<Int>) : List<Int> {
//    val output = mutableListOf<Int>()
//    for (i in instructions.indices) {
//        output.add(instructions.mapIndexed {
//            index, num ->  num * getCurrentPattern(i+1, index) }.sum().toString().takeLast(1).toInt())
//    }
//    return output
//}

fun phase2(instructions: List<Int>, pattern: Array<Int>) : List<Int> {
    val output = mutableListOf<Int>()
    for (i in instructions.indices) {
        pattern2(i+1, instructions.size, pattern)
        output.add(instructions.mapIndexed { index, num ->  num * pattern[index] }.sum().toString().takeLast(1).toInt())
    }
    return output
}

fun phase(instructions: List<Int>) : List<Int> {
    val output = mutableListOf<Int>()
    for (i in instructions.indices) {
        val pattern = pattern(i+1, instructions.size)
        output.add(instructions.mapIndexed { index, num ->  num * pattern[index] }.sum().toString().takeLast(1).toInt())
    }
    return output
}

fun pattern2(position: Int, size:Int, list: Array<Int>)  {
    var first = true
    var index = 0
    while (index < list.size) {
        BASE.forEach {
            for (i in 0 until position) {
                if (first) {
                    first = false
                    continue
                }
                if (index >= list.size) return
                list[index] = it
                index++
            }
        }
    }
}

fun pattern(position: Int, size:Int) : List<Int> {
    val list = mutableListOf<Int>()
//    PATTERNS[position]?.let {
//        return it
//    }
    while (list.size <= size) {
        BASE.forEach {
            for (i in 0 until position) {
                list.add(it)
            }
        }
    }
    list.removeAt(0)
    //PATTERNS[position] = list
    return list
}