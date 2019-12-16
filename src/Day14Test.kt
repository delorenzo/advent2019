import junit.framework.Assert.assertEquals
import org.junit.Test

class Day14Test {
    @Test
    fun first() {
        assertEquals(31, fuel("src/input/day14-input2.txt"))
    }

    @Test
    fun second() {
        assertEquals(165, fuel("src/input/day14-input3.txt"))
    }

    @Test
    fun third() {
        assertEquals(13312, fuel("src/input/day14-input4.txt"))
    }

    @Test
    fun fourth() {
        assertEquals(180697, fuel("src/input/day14-input5.txt"))
    }

    @Test
    fun fifth() {
        assertEquals(2210736, fuel("src/input/day14-input6.txt"))
    }

    @Test
    fun pt1() {
        assertEquals(843220, fuel("src/input/day14-input.txt"))
    }

//    @Test
//    fun sixth() {
//        assertEquals(82892753 , fuel("src/input/day14-input4.txt", 1000000000000))
//    }

//    @Test
//    fun seventh() {
//        assertEquals(5586022, fuel("src/input/day14-input5.txt", 1000000000000))
//    }
//
//    @Test
//    fun eight() {
//        assertEquals(460664, fuel("src/input/day14-input6.txt", 1000000000000))
//    }
}