import java.io.File
import java.util.*

const val STATIONARY = 0L
const val PULLED = 1L
fun main() {
    val initialInstructions = File("src/input/day19-input.txt").readText().trim().split(",").map { num -> num.toLong() }.toMutableList()
    initialInstructions.addAll(MutableList(1000) { 0L })

    var input = ArrayDeque<Long>()
    var relativeBase = 0L
    var ip = 0
    var affectedCount = 0
    var map = Array(5000) { Array(5000) {STATIONARY} }
    for (y in 0 until 5000L) {
        var reachedBeam = false
        loopX@ for (x in 0 until 5000L) {
            val instructions = initialInstructions.deepCopy()
            input.add(x)
            input.add(y)
            val output = run(instructions, input, ip, relativeBase)
            map[y.toInt()][x.toInt()] = output.output
            when (output.output) {
                PULLED -> {
                    reachedBeam = true
                    affectedCount++
                    if (tractorSquare(x.toInt(), y.toInt(), map)) {
                        println(closestPointInTractorSquare(x.toInt(), y.toInt()))
                        return
                    }
                }
                STATIONARY -> {
                    if (reachedBeam) {
                        break@loopX
                    }
                }
            }
        }
    }
    println("Affected range:  $affectedCount")
}

fun tractorSquare(x: Int, y: Int, map: Array<Array<Long>>) : Boolean {
    if (x-100 < 0 || y - 100 < 0) return false

    for (i in x-100 until x) {
        for (j in y-100 until y) {
            if (map[j][i] != PULLED) {
                return false
            }
        }
    }
    printStars(map)
    return true
}

private fun printStars(map:Array<Array<Long>>) {
    map.forEach {
        it.forEach { num ->
            print(num)
        }
        println()
    }
}

fun closestPointInTractorSquare(x: Int, y: Int) : Point {
    val emitter = Point(0, 0)
    var min = Double.MAX_VALUE
    var minPoint = Point(0,0)
    for (i in x-100 until x) {
        for (j in y-100 until y) {
            var currentPoint = Point(i, j)
            val distance = emitter.euclidean(currentPoint)
            if (distance < min) {
                min = distance
                minPoint = currentPoint
            }
        }
    }
    return minPoint
}