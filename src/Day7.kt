import java.io.File
import java.util.*
import javax.xml.stream.events.EndDocument

var permutations = mutableListOf<List<Long>>()

fun main() {
    println(findMax("src/input/day7-input.txt"))
    println(findMaxFeedbackLoop("src/input/day7-input.txt"))
}

fun findMax(file: String) : Long {
    permutations.clear()
    getPermutations(mutableListOf(0,1,2,3,4),5)
    var max = 0L
    permutations.forEach {
        val instructions = File(file).readText().trim().split(",").map { num -> num.toLong() }.toMutableList()
        val result = getE(instructions, 0, it)
        max = maxOf(max, result)
        //println("Permutation:  $it result:  $result")
    }
    return max
}

fun findMaxFeedbackLoop(file: String) : Long {
    permutations.clear()
    getPermutations(mutableListOf(5,6,7,8,9),5)
    var max = 0L
    permutations.forEach {
        val instructions = File(file).readText().trim().split(",").map { num -> num.toLong() }.toMutableList()
        val result = getEContinuous(instructions, 0, it)
        max = maxOf(max, result)
    }
    return max
}

fun getPermutations(input: MutableList<Long>, n: Int) {
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

fun <T> MutableList<T>.deepCopy() : MutableList<T> {
    return this.map { it }.toMutableList()
}

fun <T> MutableList<T>.swap(A: Int, B: Int) {
    val temp = this[A]
    this[A] = this[B]
    this[B] = temp
}

fun <T> List<T>.toQueue() : Queue<T> {
    val queue = ArrayDeque<T>()
    this.map { queue.add(it) }
    return queue
}

fun getE(instructions: MutableList<Long>, input: Long, phases:List<Long>) : Long {
    val instructions = Array(5) { instructions.deepCopy() }
    val A = run(instructions[0], listOf(phases[0], input).toQueue())
    val B = run(instructions[1], listOf(phases[1], A.output).toQueue())
    val C = run(instructions[2], listOf(phases[2], B.output).toQueue())
    val D = run(instructions[3], listOf(phases[3], C.output).toQueue())
    val E = run(instructions[4], listOf(phases[4], D.output).toQueue())
    return E.output
}

fun getEContinuous(instructions: MutableList<Long>, initialInput: Long, phases:List<Long>) : Long {
    val ips = Array(5){0}
    val inputs = Array(5){ ArrayDeque<Long>(listOf(phases[it])) }
    inputs.first().add(initialInput)
    val instructions = Array(5) { instructions.deepCopy() }
    var lastE = 0L
    while (true) {
        val A = run(instructions[0], inputs[0], ips[0])
        if (A.ip < 0) { return lastE }
        inputs[1].add(A.output)
        ips[0] = A.ip.toInt()
        val B = run(instructions[1], inputs[1], ips[1])
        if (B.ip < 0) { return lastE }
        inputs[2].add(B.output)
        ips[1] = B.ip.toInt()
        val C = run(instructions[2], inputs[2], ips[2])
        if (C.ip < 0) { return lastE }
        inputs[3].add(C.output)
        ips[2] = C.ip.toInt()
        val D = run(instructions[3],inputs[3], ips[3])
        if (D.ip < 0) { return lastE }
        inputs[4].add(D.output)
        ips[3] = D.ip.toInt()
        val E = run(instructions[4], inputs[4], ips[4])
        if (E.ip == -1L) {
            return lastE
        }
        lastE = E.output
        ips[4] = E.ip.toInt()
        inputs[0].add(E.output)
    }
}

data class Result (val output: Long, val ip: Long, val relativeBase: Long) {
    companion object {
        val END = Result(-1L,-1L,-1L)
    }
}

fun run(instructions: MutableList<Long>, input: Queue<Long>, initialPointer: Int = 0, initialRelativeBase: Long = 0L) : Result {
    var instructionPointer = initialPointer
    var relativeBase = initialRelativeBase

    while (true) {
        val instruction = instructions[instructionPointer].toString()
        val opcode = Opcode.valueOf(instruction.takeLast(2).toInt())
        // There are not guaranteed to be pointer values after an END opcode
        if (opcode == Opcode.END) {
            return Result.END
        }
        var newPointer = instructionPointer
        var modes : List<Mode>
        modes = when (instruction.length) {
            in 0..2 -> List(3) { Mode.POSITION }
            3 -> listOf(Mode.valueOf(instruction.first()), Mode.POSITION, Mode.POSITION)
            else -> instruction.substring(0, instruction.length - 2).toCharArray().map { Mode.valueOf(it) }.reversed()
        }
        if (modes.size < 3) {
            modes = modes + Mode.POSITION
        }
        when {
            opcode == Opcode.INPUT -> {
                opcode.compute(input.poll(), instructions[instructionPointer+1], 0, instructions, modes, instructionPointer.toLong(), relativeBase)
            }
            opcode == Opcode.OUTPUT -> {
                val (A, B) = instructions.subList(instructionPointer+1, instructionPointer+3)
                instructionPointer = updatePointer(instructionPointer, newPointer, opcode.instructions())
                val output = opcode.compute(A, B, 0, instructions, modes, instructionPointer.toLong(), relativeBase)
                return Result(output, instructionPointer.toLong(), relativeBase)
            }
            opcode == Opcode.RELATIVE_BASE_OFFSET -> {
                val A = instructions[instructionPointer+1]
                relativeBase = opcode.compute(A, 0, 0, instructions, modes, instructionPointer.toLong(), relativeBase)
            }
            opcode.instructions() < 3 -> {
                val (A, B) = instructions.subList(instructionPointer+1, instructionPointer+3)
                newPointer = opcode.compute(A, B, 0, instructions, modes, instructionPointer.toLong(), relativeBase).toInt()
            }
            else -> {
                val (A, B, C) = instructions.subList(instructionPointer+1, instructionPointer+4)
                newPointer = opcode.compute(A, B, C, instructions, modes, instructionPointer.toLong(), relativeBase).toInt()
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

