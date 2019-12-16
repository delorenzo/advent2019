import java.io.File
import kotlin.math.abs

val BASE = listOf(0, 1, 0, -1)
val PATTERNS = mutableMapOf<Int, List<Int>>()

fun main() {
    //part1("src/input/day16-input.txt")
    part2("src/input/day16-input4.txt")
}

fun part1(input: String) {
    var instructions = File(input).readText().trim().toCharArray().map { num -> num.toString().toInt() }.toList()
    //part 1
    for (i in 0 until 100) {
        instructions = phase(instructions)
        //println(instructions)
    }
    println(instructions.take(8).toString())
}

fun intToString(a: Int, b: Int) :String {
    return "$a$b"
}

fun part2(input: String) : String {
    var instructions = File(input).readText().trim().toCharArray().map { num -> num.toString().toInt() }.toList()
    val offset = instructions.take(7).map { it.toString() }.reduce { a, b -> "$a$b" }.toInt()

    instructions = repeat(instructions)
    //part 1
    var patternOffset = offset
    var offsetIndex = offset % instructions.size
    while (offsetIndex > 0) {
        patternOffset--
        offsetIndex--
    }
    for (i in 0 until 100) {
        instructions = phase(instructions, 10000, patternOffset)
        println(instructions)
    }
    //var realOffset = offset % instructions.size
    val response = StringBuilder()
    for (i in offset until offset+7) {
        var index = i % instructions.size
        response.append(instructions[index])
    }
    println(instructions)
    println(response)
    return response.toString()
}

fun repeat(instructions: List<Int>) : List<Int> {
    var bigList = mutableListOf<Int>()
    for (i in 0 until 10000) {
        bigList.addAll(instructions)
    }
    return bigList
}

fun phase(instructions: List<Int>, size: Int, patternOffset: Int) : List<Int> {
    val output = mutableListOf<Int>()
    for (i in 0 until size) {
        val pattern = pattern(i+1, size)
        var sum = 0L
        for (i in 0 until size) {
            sum += instructions[i % instructions.size] * pattern[i % instructions.size]
        }
        var digit = sum.toString().takeLast(1).toInt()
        output.add(digit)
    }
    return output
}

val cache = mutableMapOf<Pair<Int, Int>, Int>()
fun getCurrentPattern(position: Int, index: Int) : Int {
    cache[position to index]?.let {
        return it
    }
    var counter = position
    var current = 0
    for (i in 0 until index) {
        counter++
        if (counter == position) {
            current += 1 % BASE.size
            counter = 0
        }
    }
    cache[position to index] = BASE[current]
    return BASE[current]
}

fun phase2(instructions: List<Int>) : List<Int> {
    val output = mutableListOf<Int>()
    for (i in instructions.indices) {
        output.add(instructions.mapIndexed {
            index, num ->  num * getCurrentPattern(i+1, index) }.sum().toString().takeLast(1).toInt())
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

fun pattern(position: Int, size:Int) : List<Int> {
    val list = mutableListOf<Int>()
    PATTERNS[position]?.let {
        return it
    }
    while (list.size <= size) {
        BASE.forEach {
            for (i in 0 until position) {
                list.add(it)
            }
        }
    }
    list.removeAt(0)
    PATTERNS[position] = list
    return list
}