import org.junit.Test
import kotlin.test.assertEquals

class Day10Test {
    @Test
    fun example1() {
        assertEquals(8, findAsteroids("src/input/day10-input2.txt"))
    }

    @Test
    fun example2() {
        assertEquals(33, findAsteroids("src/input/day10-input3.txt"))
    }

    @Test
    fun example3() {
        assertEquals(35, findAsteroids("src/input/day10-input4.txt"))
    }

    @Test
    fun example4() {
        assertEquals(41, findAsteroids("src/input/day10-input5.txt"))
    }

    @Test
    fun example5() {
        assertEquals(210, findAsteroids("src/input/day10-input6.txt"))
    }
}