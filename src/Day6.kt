import java.io.File
import java.lang.Exception
import java.util.*

fun main() {
    var num = 0
    val pairs = mutableListOf<Pair<String, String>>()
    val planets = mutableMapOf<String, Node>()
    File("src/input/day6-input.txt").forEachLine {
        val pair = it.trim().split(")")
        pairs.add(pair.first() to pair.last())
    }
    pairs.forEach {  pair->
        val first = planets.getOrDefault(pair.first, Node(pair.first, null, mutableListOf()))
        first.orbited.add(pair.second)
        planets[pair.first] = first
        val second = planets.getOrDefault(pair.second, Node(pair.second, first, mutableListOf()))
        if (second.orbits?.id != null && second.orbits?.id != first.id) {
            throw Exception("This shouldn't happen, I think.")
        }
        if (second.orbits == null) {
            second.orbits = first
        }
        planets[pair.second] = second
        var current : Node? = first
        while (current?.orbits != null) {
            current = current.orbits
        }
    }
    planets.keys.forEach {
        val orbit = planets[it]
        var current : Node? = orbit
        while (current?.orbits != null) {
            current = current?.orbits
            num++
        }
    }

    println(num)

    // part 2
    val transfers = bfs(planets)
    println("transfers:  $transfers")
}

private fun bfs(planets: MutableMap<String, Node>): Int {
    val you = planets["YOU"]!!
    val santa = planets["SAN"]!!
    val visited = mutableMapOf<Node, Boolean>().withDefault { false }
    val queue = ArrayDeque<Node>()
    queue.add(you)
    while (!queue.isEmpty()) {
        val next = queue.poll()
        if (next == santa) {
            return next.cost -2
        }
        if (visited[next] == true) continue
        visited[next] = true
        next.orbits?.let {
            it.cost = next.cost + 1
            queue.add(it)
        }
        next.orbited.map { planets[it] }.filterNot { visited[it] == true }.forEach {
            it?.let {
                it.cost = next.cost + 1
                queue.add(it)
            }
        }
    }
    throw Exception("No :(")
}

private data class Node(val id: String, var orbits: Node?, var orbited: MutableList<String>) {
    var cost: Int = 0
}