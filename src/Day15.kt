import java.io.File
import java.util.*

const val NORTH = 1L
const val SOUTH = 2L
const val WEST = 3L
const val EAST = 4L

const val BAD = 0L
const val GOOD = 1L
const val DONE = 2L

const val WALL_CHAR = "#"
const val EMPTY_CHAR = "."
const val OXYGEN_CHAR = "E"
const val UNKNOWN = "?"

var right: Long = EAST
var goalX = 0
var goalY = 0

fun main() {
    val map = maze("src/input/day15-input.txt")

    var shortest = bfs(map)
    println("Shortest path :  $shortest")
    println("Oxygen minutes:  ${oxygen(map)}")
}

fun maze(file: String) : Array<Array<String>> {
    val instructions = File(file).readText().trim().split(",").map { num -> num.toLong() }.toMutableList()
    instructions.addAll(MutableList(10000000){0L})
    val map = Array(50) { Array(50) { UNKNOWN } }

    var input = ArrayDeque<Long>()
    var relativeBase = 0L
    var ip = 0
    input.add(EAST)
    var positionX = 25
    var positionY = 25
    while (ip >= 0) {
        var nextPositionX = positionX
        var nextPositionY = positionY
        when (input.peek()) {
            NORTH -> nextPositionY--
            EAST -> nextPositionX++
            SOUTH -> nextPositionY++
            WEST -> nextPositionX--
        }
        val output = run(instructions, input, ip, relativeBase)
        ip = output.ip.toInt()
        relativeBase = output.relativeBase
        when (output.output) {
            BAD -> {
                map[nextPositionY][nextPositionX] = WALL_CHAR
                input.clear()
                var next = nextMove(positionX, positionY, map)
                input.addAll(next)
            }
            GOOD -> {
                map[nextPositionY][nextPositionX] = EMPTY_CHAR
                positionX = nextPositionX
                positionY = nextPositionY
                if (input.isEmpty()) {
                    var next = nextMove(positionX, positionY, map)
                    input.addAll(next)
                }
            }
            DONE -> {
                map[nextPositionY][nextPositionX] = OXYGEN_CHAR
                goalX = nextPositionX
                goalY = nextPositionY
                printMap(map, positionX, positionY)
                return map
            }
        }
    }
    return map
}

private data class MapNode(val x: Int, val y: Int)  {
    var cost: Int = 0
    fun neighbors(map: Array<Array<String>>): List<MapNode> {
        return listOf(x-1 to y, x+1 to y, x to y-1, x to y+1).filterNot { x < 0 || y < 0 || x >= map.size || y >= map.size || map[y][x] == WALL_CHAR || map[y][x] == UNKNOWN }.map { MapNode(it.first, it.second) }
    }
}

private fun bfs(map: Array<Array<String>>) : Int {
    val visited = mutableMapOf<MapNode, Boolean>().withDefault { false }
    val queue = ArrayDeque<MapNode>()
    queue.add(MapNode(25, 25))
    while (!queue.isEmpty()) {
        val next = queue.poll()
        if (map[next.y][next.x] == OXYGEN_CHAR) {
            return next.cost
        }
        if (visited[next] == true) continue
        visited[next] = true
        next.neighbors(map).forEach {
            it.cost = next.cost +1
            queue.add(it)
        }
    }
    throw java.lang.Exception("No :(")
}

private fun oxygen(map: Array<Array<String>>) : Int {
    val visited = mutableMapOf<MapNode, Boolean>().withDefault { false }
    val queue = ArrayDeque<MapNode>()
    var maxCost = 0
    queue.add(MapNode(goalX, goalY))
    while (!queue.isEmpty()) {
        val next = queue.poll()
        if (visited[next] == true) continue
        visited[next] = true
        maxCost = next.cost
        next.neighbors(map).forEach {
            it.cost = next.cost +1
            queue.add(it)
        }
    }
   return maxCost
}

fun turnLeft(right: Long) : Long {
    return when (right) {
        EAST -> NORTH
        NORTH -> WEST
        WEST -> SOUTH
        SOUTH -> EAST
        else -> throw Exception()
    }
}

fun turnRight(right: Long) : Long {
    return when (right) {
        EAST -> SOUTH
        NORTH -> EAST
        WEST -> NORTH
        SOUTH -> WEST
        else -> throw Exception()
    }
}

fun nextMove(currentX: Int, currentY: Int, map: Array<Array<String>>) : List<Long> {
    val instructions = mutableListOf<Long>()
    when(right) {
        EAST -> {
            if (map[currentY][currentX+1] == UNKNOWN) {
                instructions.add(EAST)
                return instructions
            }
            if (currentY > 0 && map[currentY-1][currentX] != WALL_CHAR) {
                if (map[currentY-1][currentX+1] != WALL_CHAR && map[currentY-1][currentX+1] != UNKNOWN) {
                    right = turnRight(right)
                }
                instructions.add(NORTH)
                return instructions
            }

            else {
                right = turnLeft(right)
                return nextMove(currentX, currentY, map)
            }
        }
        NORTH -> {
            if (map[currentY-1][currentX] == UNKNOWN) {
                instructions.add(NORTH)
                return instructions
            }
            if (currentX > 0 && map[currentY][currentX-1] != WALL_CHAR) {
                if (map[currentY-1][currentX-1] != WALL_CHAR && map[currentY-1][currentX-1] != UNKNOWN) {
                    right = turnRight(right)
                }
                instructions.add(WEST)
                return instructions
            } else {
                right = turnLeft(right)
                return nextMove(currentX, currentY, map)
            }
        }
        WEST -> {
            if (map[currentY][currentX-1] == UNKNOWN) {
                instructions.add(WEST)
                return instructions
            }
            if (currentY < map.size && map[currentY+1][currentX] != WALL_CHAR) {
                if (map[currentY+1][currentX-1] != WALL_CHAR && map[currentY+1][currentX-1] != UNKNOWN) {
                    right = turnRight(right)
                }
                instructions.add(SOUTH)
                return instructions
            } else {
                right = turnLeft(right)
                return nextMove(currentX, currentY, map)
            }
        }
        SOUTH -> {
            if (map[currentY+1][currentX] == UNKNOWN) {
                instructions.add(SOUTH)
                return instructions
            }
            if (currentX < map.size && map[currentY][currentX+1] != WALL_CHAR) {
                if (map[currentY+1][currentX+1] != WALL_CHAR && map[currentY+1][currentX+1] != UNKNOWN) {
                    right = turnRight(right)
                }
                instructions.add(EAST)
                return instructions
            } else {
                right = turnLeft(right)
                return nextMove(currentX, currentY, map)
            }
        }
    }
    right = turnLeft(right)
    return nextMove(currentX, currentY, map)
}

fun printMap(map: Array<Array<String>>, currentX: Int, currentY: Int) {
    println("1234567890123456789012345789012345678901234567890123")
    map.forEachIndexed { y, row ->
        print(y.toString().padStart(2))
            row.mapIndexed { x, num ->
                if (x == currentX && y == currentY) {
                    print("O")
                }
                else if (x == 25 && y == 25) {
                    print("S")
                }
                else {
                    print(num)
                }
        }
        println()
    }
}