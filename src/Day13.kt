import java.io.File
import java.util.*

const val EMPTY = 0L
const val WALL = 1L
const val BLOCK = 2L
const val PADDLE = 3L
const val BALL = 4L
val tiles = listOf(EMPTY, WALL, BLOCK, PADDLE, BALL)

const val NEUTRAL = 0L
const val LEFT = -1L
const val RIGHT = 1L

fun main() {
    play("src/input/day13-input.txt")
}

data class Item(var x: Long, var y: Long, var tileType: Long)

fun play(file: String) {
    val instructions = File(file).readText().trim().split(",").map { num -> num.toLong() }.toMutableList()
    instructions.addAll(MutableList(10000000){0L})
    instructions[0] = 2L
    val game = Array(22) { Array(35) { EMPTY } }

    var input = ArrayDeque<Long>()
    var relativeBase = 0L
        var ip = 0
    var score = 0L

    val nextBlock = ArrayDeque<Item>()
    var target: Item? = null
    var paddle: Item? = null
    var ball: Item? = null
    input.add(NEUTRAL)
    var previousBall: Long = -1
    while (ip >= 0) {
        if (target == null && !nextBlock.isEmpty()) {
            target = nextBlock.poll()
        }
        val x = run(instructions, input, ip, relativeBase)
        ip = x.ip.toInt()
        if (ip < 0) break
        relativeBase = x.relativeBase
        val y = run(instructions, input, ip, relativeBase)
        ip = y.ip.toInt()
        if (ip < 0) break
        relativeBase = y.relativeBase
        val tile = run(instructions, input, ip, relativeBase)

        if (tile.output == BLOCK) {
            nextBlock.add(Item(x.output, y.output, BLOCK))
        }
        if (tile.output == PADDLE) {
            paddle = Item(x.output, y.output, PADDLE)
        }
        if (tile.output == BALL) {
            if (ball != null) {
                previousBall = ball.x
            }
            ball = Item(x.output, y.output, BALL)
        }
        ip = tile.ip.toInt()
        relativeBase = tile.relativeBase
        if (x.output == -1L && y.output == 0L) {
            score = tile.output
            println("Score updated:  $score")
        } else {
            game[y.output.toInt()][x.output.toInt()] = tile.output
        }

        if (ball != null  && paddle != null) {
            input.poll()
            println("Ball:  $ball  paddle:  $paddle")
            println("input.size:  ${input.size}")
            if (paddle.x < ball.x) {
                input.add(RIGHT)
            }
            else if (paddle.x > ball.x) {
                input.add(LEFT)
            }
            else {
                if (previousBall != -1L) {
                    if (previousBall < ball.x && (ball.y - paddle.y > 1)) {
                        input.add(RIGHT)
                    }
                    if (previousBall > ball.x && (ball.y - paddle.y > 1)) {
                        input.add(LEFT)
                    }
                    else {
                        input.add(NEUTRAL)
                    }
                } else {
                    input.add(NEUTRAL)
                }
            }
            game.forEach {
                it.map { num -> print(num) }
                println()
            }
        }
    }

    val blocks = game.sumBy { it -> it.filter { it == BLOCK }.size }
    println("Blocks :: $blocks")
    println("Score :: $score")

}