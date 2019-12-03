import org.junit.Test
import kotlin.test.assertEquals

class Day3Tests() {
    @Test
    fun one() {
        assertEquals(6 to 30, wires("src/input/day3-input2.txt"))
    }

    @Test
    fun two() {
        assertEquals(159 to 610, wires("src/input/day3-input3.txt"))
    }

    @Test
    fun tree() {
        assertEquals(135 to 410, wires("src/input/day3-input4.txt") )
    }

    @Test
    fun realInput() {
        assertEquals(3247 to 48054, wires("src/input/day3-input.txt"))
    }
}