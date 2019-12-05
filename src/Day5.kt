import java.io.File

fun main() {
    Day5("src/input/day5-input.txt", 5)
}

fun Day5(file: String, input: Int) {
    val instructions = File(file).readText().trim().split(",").map { it.toInt() }.toMutableList()
    var instructionPointer = 0

    while (true) {
        val instruction = instructions[instructionPointer].toString()
        val opcode = Opcode.valueOf(instruction.takeLast(2).toInt())
        // There are not guaranteed to be pointer values after an END opcode
        if (opcode == Opcode.END) {
            opcode.compute(registers = instructions)
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
                opcode.compute(input, instructions[instructionPointer+1], 0, instructions)
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
        instructionPointer = when (newPointer) {
            instructionPointer -> instructionPointer + opcode.instructions()+1
            else -> newPointer
        }
    }
}

enum class Mode(val num: Int) {
    POSITION(0) {
        override fun getValue(num: Int, registers: MutableList<Int>) : Int {
            return registers[num]
        }
    },
    IMMEDIATE(1) {
        override fun getValue(num: Int, registers: MutableList<Int>): Int {
            return num
        }
    };

    abstract fun getValue(num: Int, registers: MutableList<Int>) : Int

    companion object {
        fun valueOf(int: Int): Mode {
            return when (int) {
                0 -> POSITION
                1 -> IMMEDIATE
                else -> throw Exception("problemo here")
            }
        }
        fun valueOf(char: Char): Mode {
            return when (char) {
                '0' -> POSITION
                '1' -> IMMEDIATE
                else -> throw Exception("problemo here")
            }
        }
    }
}