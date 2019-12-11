import java.io.File
import java.util.*

private const val BLACK_LONG = 0L
private const val WHITE_LONG = 1L
const val SIZE = 1000

fun main() {
    run("src/input/day11-input.txt")
}

fun <T> Array<Array<T>>.get(p: Point) : T {
    return this[p.y][p.x]
}

fun <T> Array<Array<T>>.put(p: Point, value: T) {
    this[p.y][p.x] = value
}

fun run(file: String) {
    val instructions = File(file).readText().trim().split(",").map { num -> num.toLong() }.toMutableList()
    instructions.addAll(MutableList(10000000){0L})
    val painted = mutableMapOf<Point, Boolean>()
    val ship = Array(SIZE) { Array (SIZE) { BLACK_LONG } }

    val robot = Robot()
    ship.put(robot.currentPosition, WHITE_LONG)

    var input = ArrayDeque<Long>()
    var relativeBase = 0L
    var ip = 0
    input.add(ship.get(robot.currentPosition))
    while (!input.isEmpty()) {
        val paint = run(instructions, input, ip, relativeBase)
        if (paint == Result.END) break
        val turn = run(instructions, input, paint.ip.toInt(), paint.relativeBase)
        if (turn == Result.END) break
        ip = turn.ip.toInt()
        relativeBase = paint.relativeBase
        ship.put(robot.currentPosition, paint.output)
        if (!painted.getOrDefault(robot.currentPosition, false)) {
            painted[robot.currentPosition] = true
            robot.areaCovered++
        }
        robot.currentDirection = robot.currentDirection.turn(turn.output)
        robot.currentPosition = robot.currentDirection.nextPosition(robot.currentPosition)
        input.add(ship.get(robot.currentPosition))
    }
    println("Robot painted ${robot.areaCovered} squares.")

    val minX = painted.minBy { it.key.x }!!.key.x-2
    val maxX = painted.maxBy { it.key.x }!!.key.x+2
    val minY = painted.minBy { it.key.y }!!.key.y-2
    val maxY = painted.maxBy { it.key.y }!!.key.y+2
    for (i in minY until maxY) {
        for (j in minX until maxX) {
            if(ship[i][j] == WHITE_LONG) { print("#") }
            else { print(".") }
        }
        println()
    }
}

private data class Robot(var areaCovered: Int = 0,
                         var currentDirection:Direction = Direction.NORTH,
                         var currentPosition: Point = Point(SIZE/2,SIZE/2))

enum class Direction {
    NORTH {
        override fun turnLeft(): Direction {
            return WEST
        }

        override fun turnRight(): Direction {
            return EAST
        }

        override fun nextPosition(p: Point): Point {
            return Point(p.x, p.y-1)
        }
    },
    EAST {
        override fun turnLeft(): Direction {
            return NORTH
        }

        override fun turnRight(): Direction {
            return SOUTH
        }

        override fun nextPosition(p: Point): Point {
            return Point(p.x+1, p.y)
        }
    },
    WEST {
        override fun turnLeft(): Direction {
            return SOUTH
        }

        override fun turnRight(): Direction {
            return NORTH
        }
        override fun nextPosition(p: Point): Point {
            return Point(p.x-1, p.y)
        }
    },
    SOUTH {
        override fun turnLeft(): Direction {
            return EAST
        }

        override fun turnRight(): Direction {
            return WEST
        }

        override fun nextPosition(p: Point): Point {
            return Point(p.x, p.y+1)
        }
    };

    private val right = 1L
    private val left = 0L

    fun turn(direction: Long) : Direction {
        return when (direction) {
            left -> this.turnLeft()
            right -> this.turnRight()
            else -> throw Exception("Invalid turning direction")
        }
    }

    abstract fun turnLeft() : Direction
    abstract fun turnRight() : Direction
    abstract fun nextPosition(p: Point) : Point
}