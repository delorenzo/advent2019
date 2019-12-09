import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Day9Test {
    @Test
    fun example1() {
        assertEquals(109L, BOOST("src/input/day9-input2.txt"))
    }

    @Test
    fun example2() {
        val result = BOOST("src/input/day9-input3.txt")

        assertTrue(result.toString().length == 16, "Result is a 16digit number")
    }

    @Test
    fun example3() {
        assertEquals(1125899906842624L, BOOST("src/input/day9-input4.txt"))
    }

    @Test
    fun part1() {
        assertEquals(2714716640, BOOST("src/input/day9-input.txt", 1))
    }

    @Test
    fun part2() {
        assertEquals(58879, BOOST("src/input/day9-input.txt", 2))
    }
}