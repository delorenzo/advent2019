import java.io.File
import java.util.*

var permutations = mutableListOf<List<Int>>()

fun main() {
    println(findMax("src/input/day7-input.txt"))
    println(findMaxFeedbackLoop("src/input/day7-input.txt"))
}

fun findMax(file: String) : Int {
    permutations.clear()
    getPermutations(mutableListOf(0,1,2,3,4),5)
    var max = 0
    permutations.forEach {
        val instructions = File(file).readText().trim().split(",").map { num -> num.toInt() }.toMutableList()
        val result = getE(instructions, 0, it)
        max = maxOf(max, result)
        //println("Permutation:  $it result:  $result")
    }
    return max
}

fun findMaxFeedbackLoop(file: String) : Int {
    permutations.clear()
    getPermutations(mutableListOf(5,6,7,8,9),5)
    var max = 0
    permutations.forEach {
        val instructions = File(file).readText().trim().split(",").map { num -> num.toInt() }.toMutableList()
        val result = getEContinuous(instructions, 0, it)
        max = maxOf(max, result)
    }
    return max
}

fun getPermutations(input: MutableList<Int>, n: Int) {
    if (n == 1) {
        permutations.add(input.deepCopy())
        //println(input)
        return
    }
    getPermutations(input, n-1)
    for (i in 0 until n-1) {
        when {
            n%2 == 0 -> input.swap(n-1, i)
            else -> input.swap(n-1, 0)
        }
        getPermutations(input, n-1)
    }
}

fun MutableList<Int>.deepCopy() : MutableList<Int> {
    return this.map { it }.toMutableList()
}

fun MutableList<Int>.swap(A: Int, B: Int) {
    val temp = this[A]
    this[A] = this[B]
    this[B] = temp
}

fun List<Int>.toQueue() : Queue<Int> {
    val queue = ArrayDeque<Int>()
    this.map { queue.add(it) }
    return queue
}

fun getE(instructions: MutableList<Int>, input: Int, phases:List<Int>) : Int {
    val instructions = Array(5) { instructions.deepCopy() }
    val A = run(instructions[0], listOf(phases[0], input).toQueue())
    val B = run(instructions[1], listOf(phases[1], A.first).toQueue())
    val C = run(instructions[2], listOf(phases[2], B.first).toQueue())
    val D = run(instructions[3], listOf(phases[3], C.first).toQueue())
    val E = run(instructions[4], listOf(phases[4], D.first).toQueue())
    return E.first
}

fun getEContinuous(instructions: MutableList<Int>, initialInput: Int, phases:List<Int>) : Int {
    val ips = Array(5){0}
    val inputs = Array(5){ ArrayDeque<Int>(listOf(phases[it])) }
    inputs.first().add(initialInput)
    val instructions = Array(5) { instructions.deepCopy() }
    var lastE = 0
    while (true) {
        val A = run(instructions[0], inputs[0], ips[0])
        if (A.second < 0) { return lastE }
        inputs[1].add(A.first)
        ips[0] = A.second
        val B = run(instructions[1], inputs[1], ips[1])
        if (B.second < 0) { return lastE }
        inputs[2].add(B.first)
        ips[1] = B.second
        val C = run(instructions[2], inputs[2], ips[2])
        if (C.second < 0) { return lastE }
        inputs[3].add(C.first)
        ips[2] = C.second
        val D = run(instructions[3],inputs[3], ips[3])
        if (D.second < 0) { return lastE }
        inputs[4].add(D.first)
        ips[3] = D.second
        val E = run(instructions[4], inputs[4], ips[4])
        if (E.second == -1) {
            return lastE
        }
        lastE = E.first
        ips[4] = E.second
        inputs[0].add(E.first)
    }
}

fun run(instructions: MutableList<Int>, input: Queue<Int>, initialPointer: Int = 0) : Pair<Int,Int> {
    var instructionPointer = initialPointer

    while (true) {
        val instruction = instructions[instructionPointer].toString()
        val opcode = Opcode.valueOf(instruction.takeLast(2).toInt())
        // There are not guaranteed to be pointer values after an END opcode
        if (opcode == Opcode.END) {
            return -1 to -1
        }
        var newPointer = instructionPointer
        var modes : List<Mode>
        modes = when (instruction.length) {
            in 0..2 -> List(3) {Mode.POSITION}
            3 -> listOf(Mode.valueOf(instruction.first()), Mode.POSITION, Mode.POSITION)
            else -> instruction.substring(0, instruction.length - 2).toCharArray().map { Mode.valueOf(it) }.reversed()
        }
        when {
            opcode == Opcode.INPUT -> {
                opcode.compute(input.poll(), instructions[instructionPointer+1], 0, instructions)
            }
            opcode == Opcode.OUTPUT -> {
                val (A, B) = instructions.subList(instructionPointer+1, instructionPointer+3)
                instructionPointer = updatePointer(instructionPointer, newPointer, opcode.instructions())
                return opcode.compute(A, B, 0, instructions, modes, instructionPointer) to instructionPointer
            }
            opcode.instructions() < 3 -> {
                val (A, B) = instructions.subList(instructionPointer+1, instructionPointer+3)
                newPointer = opcode.compute(A, B, 0, instructions, modes, instructionPointer)
            }
            else -> {
                val (A, B, C) = instructions.subList(instructionPointer+1, instructionPointer+4)
                newPointer = opcode.compute(A, B, C, instructions, modes, instructionPointer)
            }
        }
        instructionPointer = updatePointer(instructionPointer, newPointer, opcode.instructions())
    }
}

private fun updatePointer(oldPointer : Int, newPointer: Int, instructions: Int) : Int {
    return when (newPointer) {
        oldPointer -> oldPointer + instructions+1
        else -> newPointer
    }
}

