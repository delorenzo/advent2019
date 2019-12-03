import java.io.File
import kotlin.math.absoluteValue

lateinit var port : Point
var steps = 1
lateinit var stepMap: MutableMap<Pair<Int,Int>, MutableList<Pair<Int,Int>>>

fun main() {
    wires("src/input/day3-input2.txt")
}

fun wires(file: String) : Pair<Int, Int> {
    val max = 20000
    val half = max/2
    port = Point(half, half)
    val intersectionMap = mutableMapOf<Pair<Int,Int>, Int>().withDefault { -1 }
    val wireMap = mutableMapOf<Pair<Int,Int>, Int>().withDefault { -1 }
    stepMap = mutableMapOf<Pair<Int,Int>, MutableList<Pair<Int,Int>>>().withDefault { mutableListOf() }
    var id = 0
    File(file).forEachLine {
        steps = 1
        val directions = it.trim().split(",")
        var current = port
        for (direction in directions) {
            val magnitude = direction.substring(1, direction.length).toInt()
            when(direction[0]) {
                'R' -> current = goRight(current, magnitude, id, intersectionMap, wireMap)
                'D' -> current = goDown(current, magnitude, id, intersectionMap, wireMap)
                'L' -> current = goLeft(current, magnitude, id, intersectionMap, wireMap)
                'U' -> current = goUp(current, magnitude, id, intersectionMap, wireMap)
            }
        }
        id++
    }
    val intersections = intersectionMap.filterValues { it > 0 }.keys.map { Point(it.first, it.second) }
    val closest = intersections.minBy { it.portDistance }!!.portDistance
    val steppiest = combinedSteps(intersections.minBy { combinedSteps(it) }!!)
    println(closest)
    println(steppiest)
    return closest to steppiest
}

private fun combinedSteps(point: Point): Int {
   return stepMap[point.x to point.y]!!.map { it.second }.sum()
}

private fun goRight(current: Point, num: Int, id: Int,
                    intersections: MutableMap<Pair<Int, Int>, Int>,
                    wires: MutableMap<Pair<Int, Int>, Int>) : Point {
    for (i in 1 until num+1) {
        val s = stepMap.getOrDefault(current.x + i to current.y, mutableListOf())
        if (s.none{it.first == id }) {
            s.add(id to steps)
            stepMap[current.x + i to current.y] = s
        }
        if (current.x + i == port.x && current.y == port.y) continue
        if (wires[current.x + i to current.y] != id) {
            if (intersections.getOrDefault(current.x + i to current.y, -1) == -1) {
                wires[current.x + i to current.y] = id
                intersections[current.x + i to current.y] = 0
            } else {
                intersections[current.x + i to current.y] = 1
            }
        }
        steps++
    }
    return Point(current.x+num, current.y)
}

private fun goLeft(current: Point, num: Int, id: Int,
                    intersections: MutableMap<Pair<Int, Int>, Int>,
                    wires: MutableMap<Pair<Int, Int>, Int>) : Point {
    for (i in 1 until num+1) {
        val s = stepMap.getOrDefault(current.x -i to current.y, mutableListOf())
        if (s.none{it.first == id }) {
            s.add(id to steps)
            stepMap[current.x - i to current.y] = s
        }
        if (current.x - i == port.x && current.y == port.y) continue
        if (wires[current.x - i to current.y] != id) {
            if (intersections.getOrDefault(current.x - i to current.y, -1) == -1) {
                wires[current.x - i to current.y] = id
                intersections[current.x - i to current.y] = 0
            } else {
                intersections[current.x - i to current.y] = 1
            }
        }
        steps++
    }
    return Point(current.x-num, current.y)
}

private fun goUp(current: Point, num: Int, id: Int,
                    intersections: MutableMap<Pair<Int, Int>, Int>,
                    wires: MutableMap<Pair<Int, Int>, Int>) : Point {
    for (i in 1 until num+1) {
        val s = stepMap.getOrDefault(current.x to current.y+i, mutableListOf())
        if (s.none{it.first == id }) {
            s.add(id to steps)
            stepMap[current.x to current.y+i] = s
        }
        if (current.x == port.x && current.y+i == port.y) continue
        if (wires[current.x to current.y+i] != id) {
            if (intersections.getOrDefault(current.x to current.y+i, -1) == -1) {
                wires[current.x to current.y+i] = id
                intersections[current.x to current.y+i] = 0
            } else {
                intersections[current.x to current.y+i] = 1
            }
        }
        steps++
    }
    return Point(current.x, current.y+num)
}

private fun goDown(current: Point, num: Int, id: Int,
                    intersections: MutableMap<Pair<Int, Int>, Int>,
                    wires: MutableMap<Pair<Int, Int>, Int>) : Point {
    for (i in 1 until num+1) {
        val s = stepMap.getOrDefault(current.x to current.y-i, mutableListOf())
        if (s.none{it.first == id }) {
            s.add(id to steps)
            stepMap[current.x to current.y-i] = s
        }
        if (current.x == port.x && current.y-i == port.y) continue
        if (wires[current.x to current.y-i] != id) {
            if (intersections.getOrDefault(current.x to current.y-i, -1) == -1) {
                wires[current.x to current.y-i] = id
                intersections[current.x to current.y-i] = 0
            } else {
                intersections[current.x to current.y-i] = 1
            }
        }
        steps++
    }
    return Point(current.x, current.y-num)
}

data class Point(val x: Int, val y: Int) {
    val portDistance : Int by lazy {
        manhattan(port)
    }
    private fun manhattan(other: Point) : Int {
        return (this.x-other.x).absoluteValue + (this.y-other.y).absoluteValue
    }
}