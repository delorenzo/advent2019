import java.io.File

fun main() {
    val modules = File("src/input/day1-input.txt").readLines().map { it.trim().toInt() }
    val total = modules.map { findFuel(it) }.sum()
    println(total)
}

fun findFuel(mass: Int) : Int {
    if (mass <= 0 ) return 0
    val fuel = maxOf(0, mass/3 - 2)
    return fuel + findFuel(fuel)
}