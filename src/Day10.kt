import java.io.File

private const val ASTEROID = '#'

fun main() {
    println(findAsteroids("src/input/day10-input.txt"))
}

fun findAsteroids(file: String) : Int {
    val asteroids = mutableListOf<Point>()
    var y = 0
    File(file).forEachLine {
        val row = it.toCharArray()
        row.forEachIndexed { x, char -> if (char == ASTEROID) { asteroids.add(Point(x, y))}}
        y++
    }

    //part 1
    var max = 0
    var bestAsteroid = asteroids[0]
    var orbit = listOf<Point>()
    asteroids.forEach { point ->
        val asteroids = getCount(point, asteroids.sortedBy { it.euclidean(point) }.filterNot { it == point })
        if (asteroids.size > max) {
            max = asteroids.size
            bestAsteroid = point
            orbit = asteroids
        }
    }
    println("Best boi:  $bestAsteroid")

    //part 2
    var count = 0
    var killThem = orbit.filterNot{it == bestAsteroid}
    var lastSploded = orbit.first()
    val clockwiseSorting = killThem.sortedBy {
        point -> kotlin.math.atan2(
            (point.y - bestAsteroid.y).toDouble(),
            (point.x - bestAsteroid.x).toDouble()); }
    val first = clockwiseSorting.indexOfFirst { it.x == bestAsteroid.x }
    println("Index of top of clock:  $first")
    var current = first
    while (count < 200) {
        count++
        lastSploded = clockwiseSorting[current]
        println("#$count>>$lastSploded")
        current++
        if (current >= clockwiseSorting.size) {
            current = 0
        }
    }
    val answer = lastSploded.x * 100 + lastSploded.y
    println("Answer:  $answer")
    return max
}

fun getCount(p: Point, asteroids: List<Point>): List<Point>{
    val distances = mutableListOf<Point>()
    asteroids.forEach {asteroid ->
        if (distances.isEmpty() || !distances.any { p.isBetween(it, asteroid) }) {
            distances.add(asteroid)
        }
    }
    return distances
}
