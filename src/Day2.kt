import java.io.File
import kotlin.system.exitProcess

fun main() {
    val instructions = File("src/input/day2-input.txt").readText().trim().split(",").map { it.toLong() }.toMutableList()
    val partOneInput = 12L to 2L
    val noun = 52L // multiplier
    val verb = 96L // constant added to the end

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
        println("$opcode $A (${instructions[A.toInt()]}) $B (${instructions[B.toInt()]}) $C (${instructions[C.toInt()]})")
        opcode.compute(A, B, C, instructions)
        nextOpcode += 4
    }
}

enum class Opcode(val num: Int) {
    ADD(1) {
        override fun compute(A: Long, B: Long, C: Long, registers: MutableList<Long>,  modes : List<Mode>, ip: Long, relativeBase: Long) : Long{
            registers[modes[2].getPosition(C, registers, relativeBase)] = modes[0].getValue(A, registers, relativeBase) + modes[1].getValue(B, registers, relativeBase)
            return ip
        }
        override fun instructions(): Int {
            return 3
        }
    },
    MUL(2) {
        override fun compute(A: Long, B: Long, C: Long, registers: MutableList<Long>,  modes : List<Mode>, ip: Long, relativeBase: Long) : Long {
            registers[modes[2].getPosition(C, registers, relativeBase)] =  modes[0].getValue(A, registers, relativeBase) * modes[1].getValue(B, registers, relativeBase)
            return ip
        }
        override fun instructions(): Int {
            return 3
        }
    },
    INPUT(3) {
        override fun compute(A: Long, B: Long, C: Long, registers: MutableList<Long>,  modes : List<Mode>, ip: Long, relativeBase: Long) : Long {
            registers[modes[0].getPosition(B, registers, relativeBase)] = A
            return ip
        }

        override fun instructions(): Int {
            return 1
        }
    },
    OUTPUT(4) {
        override fun compute(A: Long, B: Long, C: Long, registers: MutableList<Long>,  modes : List<Mode>, ip: Long, relativeBase: Long) : Long {
            val result = modes[0].getValue(A, registers, relativeBase)
            println(result)
            return result
        }

        override fun instructions(): Int {
            return 1
        }
    },
    JUMP_IF_TRUE(5) {
        override fun instructions(): Int {
            return 2
        }

        override fun compute(A: Long, B: Long, C: Long, registers: MutableList<Long>, modes: List<Mode>, ip: Long, relativeBase: Long) : Long {
            return if (modes[0].getValue(A, registers, relativeBase) != 0L) {
                modes[1].getValue(B, registers, relativeBase)
            } else {
                ip
            }
        }
    },
    JUMP_IF_FALSE(6) {
        override fun instructions(): Int {
            return 2
        }

        override fun compute(A: Long, B: Long, C: Long, registers: MutableList<Long>, modes: List<Mode>, ip: Long, relativeBase: Long): Long {
            return if (modes[0].getValue(A, registers, relativeBase) == 0L) {
                modes[1].getValue(B, registers, relativeBase)
            } else {
                ip
            }
        }
    },
    LESS_THAN(7) {
        override fun instructions(): Int {
            return 3
        }

        override fun compute(A: Long, B: Long, C: Long, registers: MutableList<Long>, modes: List<Mode>, ip: Long, relativeBase: Long): Long {
            registers[modes[2].getPosition(C, registers, relativeBase)] = when (modes[0].getValue(A, registers, relativeBase) < modes[1].getValue(B, registers, relativeBase)) {
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

        override fun compute(A: Long, B: Long, C: Long, registers: MutableList<Long>, modes: List<Mode>, ip: Long, relativeBase: Long): Long {
            registers[modes[2].getPosition(C, registers, relativeBase)] = when (modes[0].getValue(A, registers, relativeBase) == modes[1].getValue(B, registers, relativeBase)) {
                true -> 1
                false -> 0
            }
            return ip
        }
    },
    RELATIVE_BASE_OFFSET(9) {
        override fun instructions(): Int {
            return 1
        }

        override fun compute(A: Long, B: Long, C: Long, registers: MutableList<Long>, modes: List<Mode>, ip: Long, relativeBase: Long): Long {
            return modes[0].getValue(A, registers, relativeBase) + relativeBase
        }
    },
    END(99) {
        override fun compute(A: Long, B: Long, C: Long, registers: MutableList<Long>, modes : List<Mode>, ip: Long, relativeBase: Long) : Long {
            println("Program ended.")
            println("Value at 0 is ${registers[0]}")
            exitProcess(0)
        }

        override fun instructions(): Int {
            return 0
        }
    };
    abstract fun instructions(): Int
    abstract fun compute(A: Long = 0, B: Long = 0, C: Long = 0, registers: MutableList<Long>,
                         modes : List<Mode> = List(3) {Mode.POSITION}, ip: Long = 0, relativeBase: Long = 0) : Long

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
                9 -> RELATIVE_BASE_OFFSET
                99 -> END
                else -> throw Exception("problemo here")
            }
        }

        fun valueOf(long: Long): Opcode {
            return when (long) {
                1L -> ADD
                2L -> MUL
                3L -> INPUT
                4L -> OUTPUT
                5L -> JUMP_IF_TRUE
                6L -> JUMP_IF_FALSE
                7L -> LESS_THAN
                8L -> EQUALS
                9L -> RELATIVE_BASE_OFFSET
                99L -> END
                else -> throw Exception("problemo here")
            }
        }

        fun valueOf(str: String): Opcode {
            return valueOf(str.toInt())
        }
    }
}