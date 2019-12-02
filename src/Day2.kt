import java.io.File
import kotlin.system.exitProcess

fun main() {
    val instructions = File("src/input/day2-input.txt").readText().trim().split(",").map { it.toInt() }.toMutableList()
    val noun = 52 // multiplier
    val verb = 96 // constant added to the end

    instructions[1] = noun
    instructions[2] = verb
    var nextOpcode = 0

    for (i in 0 until instructions.size-3 step 4) {
        val opcode = Opcode.valueOf(instructions[i])
        println("$i to ${i+3}")
        println("$opcode ${instructions[i+1]} ${instructions[i+2]} ${instructions[i+3]}")

    }

    println("----")
    println("----")
    println("----")

    while (true) {
        val opcode = Opcode.valueOf(instructions[nextOpcode])
        // There are not guaranteed to be pointer values after an END opcode
        if (opcode == Opcode.END) {
            opcode.compute(registers = instructions)
        }
        val (A, B, C) = instructions.subList(nextOpcode+1, nextOpcode+4)
        println("$opcode $A (${instructions[A]}) $B (${instructions[B]}) $C (${instructions[C]})")
        opcode.compute(A, B, C, instructions)
        nextOpcode += 4
    }
}

private enum class Opcode(val num: Int) {
    ADD(1) {
        override fun compute(A: Int, B: Int, C: Int, registers: MutableList<Int>){
            registers[C] = registers[A] + registers[B]
        }
    },
    MUL(2) {
        override fun compute(A: Int, B: Int, C: Int, registers: MutableList<Int>) {
            registers[C] = registers[A] * registers[B]
        }
    },
    END(99) {
        override fun compute(A: Int, B: Int, C: Int, registers: MutableList<Int>) {
            println("Program ended.")
            println("Value at 0 is ${registers[0]}")
            exitProcess(0)
        }
    };
    abstract fun compute(A: Int = 0, B: Int = 0, C: Int = 0, registers: MutableList<Int>)

    companion object {
        fun valueOf(int: Int): Opcode {
            return when (int) {
                1 -> ADD
                2 -> MUL
                99 -> END
                else -> throw Exception("problemo here")
            }
        }
    }
}