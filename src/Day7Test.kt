import org.junit.Test
import kotlin.test.assertEquals

class Day7Test {
    @Test
    fun part1_example1() {
        assertEquals(43210, findMax("src/input/day7-input2.txt"))
    }

    @Test
    fun part1_example2() {
        assertEquals(54321, findMax("src/input/day7-input3.txt"))
    }

    @Test
    fun part1_example3() {
        assertEquals(65210, findMax("src/input/day7-input4.txt"))
    }

    @Test
    fun part1() {
        assertEquals(255840, findMax("src/input/day7-input.txt"))
    }

    @Test
    fun part2_example1() {
        assertEquals(139629729, findMaxFeedbackLoop("src/input/day7-input5.txt"))
    }

    @Test
    fun part2_example2() {
        assertEquals(18216, findMaxFeedbackLoop("src/input/day7-input6.txt"))
    }

    @Test
    fun part2() {
        assertEquals(84088865, findMaxFeedbackLoop("src/input/day7-input.txt"))
    }
}