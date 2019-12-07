import java.io.File
import kotlin.system.exitProcess

fun main() {
    val instructions = File("src/input/day2-input.txt").readText().trim().split(",").map { it.toInt() }.toMutableList()
    val partOneInput = 12 to 2
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

enum class Opcode(val num: Int) {
    ADD(1) {
        override fun compute(A: Int, B: Int, C: Int, registers: MutableList<Int>,  modes : List<Mode>, ip: Int) : Int{
            registers[C] = modes[0].getValue(A, registers) + modes[1].getValue(B, registers)
            return ip
        }
        override fun instructions(): Int {
            return 3
        }
    },
    MUL(2) {
        override fun compute(A: Int, B: Int, C: Int, registers: MutableList<Int>,  modes : List<Mode>, ip: Int) : Int {
            registers[C] =  modes[0].getValue(A, registers) * modes[1].getValue(B, registers)
            return ip
        }
        override fun instructions(): Int {
            return 3
        }
    },
    INPUT(3) {
        override fun compute(A: Int, B: Int, C: Int, registers: MutableList<Int>,  modes : List<Mode>, ip: Int) : Int {
            registers[B] = A
            return ip
        }

        override fun instructions(): Int {
            return 1
        }
    },
    OUTPUT(4) {
        override fun compute(A: Int, B: Int, C: Int, registers: MutableList<Int>,  modes : List<Mode>, ip: Int) : Int {
            return modes[0].getValue(A, registers)
        }

        override fun instructions(): Int {
            return 1
        }
    },
    JUMP_IF_TRUE(5) {
        override fun instructions(): Int {
            return 2
        }

        override fun compute(A: Int, B: Int, C: Int, registers: MutableList<Int>, modes: List<Mode>, ip: Int) : Int {
            return if (modes[0].getValue(A, registers) != 0) {
                modes[1].getValue(B, registers)
            } else {
                ip
            }
        }
    },
    JUMP_IF_FALSE(6) {
        override fun instructions(): Int {
            return 2
        }

        override fun compute(A: Int, B: Int, C: Int, registers: MutableList<Int>, modes: List<Mode>, ip: Int): Int {
            return if (modes[0].getValue(A, registers) == 0) {
                modes[1].getValue(B, registers)
            } else {
                ip
            }
        }
    },
    LESS_THAN(7) {
        override fun instructions(): Int {
            return 3
        }

        override fun compute(A: Int, B: Int, C: Int, registers: MutableList<Int>, modes: List<Mode>, ip: Int): Int {
            registers[C] = when (modes[0].getValue(A, registers) < modes[1].getValue(B, registers)) {
                true -> 1
                false -> 0
            }
            return ip
        }
    },
    EQUALS(8) {
        override fun instructions(): Int {
            return 3
        }

        override fun compute(A: Int, B: Int, C: Int, registers: MutableList<Int>, modes: List<Mode>, ip: Int): Int {
            registers[C] = when (modes[0].getValue(A, registers) == modes[1].getValue(B, registers)) {
                true -> 1
                false -> 0
            }
            return ip
        }
    },
    END(99) {
        override fun compute(A: Int, B: Int, C: Int, registers: MutableList<Int>, modes : List<Mode>, ip: Int) : Int {
            println("Program ended.")
            println("Value at 0 is ${registers[0]}")
            exitProcess(0)
        }

        override fun instructions(): Int {
            return 0
        }
    };
    abstract fun instructions(): Int
    abstract fun compute(A: Int = 0, B: Int = 0, C: Int = 0, registers: MutableList<Int>, modes : List<Mode> = List(3) {Mode.POSITION}, ip: Int = 0) : Int

    companion object {
        fun valueOf(int: Int): Opcode {
            return when (int) {
                1 -> ADD
                2 -> MUL
                3 -> INPUT
                4 -> OUTPUT
                5 -> JUMP_IF_TRUE
                6 -> JUMP_IF_FALSE
                7 -> LESS_THAN
                8 -> EQUALS
                99 -> END
                else -> throw Exception("problemo here")
            }
        }

        fun valueOf(str: String): Opcode {
            return valueOf(str.toInt())
        }
    }
}