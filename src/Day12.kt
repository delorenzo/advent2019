import java.io.File
import kotlin.math.absoluteValue

fun main() {
    simulateMotion("src/input/day12-input.txt")
}

//35975
//18088521

data class Vector(var x: Int, var y: Int, var z: Int) {
    operator fun plus(increment: Vector) : Vector {
        return Vector(this.x + increment.x, this.y + increment.y, this.z + increment.z)
    }
}
data class Planet(var position: Vector, var velocity: Vector = Vector(0,0,0))

fun repeating(planets: List<Planet>, previous: List<Vector>) : Boolean {
    if (planets.size != previous.size) return false
    for (i in planets.indices) {
        if (planets[i].position != previous[i]) {
            return false
        }
    }
    return true
}

fun simulateMotion(file: String) {
    val regex = Regex("<x=(\\-?[0-9]+), y=(\\-?[0-9]+), z=(\\-?[0-9]+)>")
    val moons = mutableListOf<Planet>()
    File(file).forEachLine {
        val match = regex.matchEntire(it.trim())
        val groups = match!!.groupValues.takeLast(3).map { group -> group.toInt() }
        moons.add(Planet(Vector(groups[0], groups[1], groups[2])))
    }
    var previousMoons = MutableList(4) { Vector(0,0,0)}
    for (i in 1 until 46867749240000) {
        if (i % 10000000L == 0L) {
            println("Step#$i")
        }
        applyGravity(moons)
        applyVelocity(moons, previousMoons)
        if (repeating(moons, previousMoons)) {
            val stepsEstimated = i*2
            println("Took $stepsEstimated steps.")
            return
        }
    }
    println(totalEnergy(moons))
}

fun applyGravity(moons: List<Planet>) {
    for (i in moons.indices) {
        for (j in i+1 until moons.size) {
            when {
                moons[i].position.x < moons[j].position.x -> {
                    moons[i].velocity.x += 1
                    moons[j].velocity.x -= 1
                }
                moons[i].position.x > moons[j].position.x -> {
                    moons[i].velocity.x -= 1
                    moons[j].velocity.x += 1
                }
            }
            when {
                moons[i].position.y < moons[j].position.y -> {
                    moons[i].velocity.y += 1
                    moons[j].velocity.y -= 1
                }
                moons[i].position.y > moons[j].position.y -> {
                    moons[i].velocity.y -= 1
                    moons[j].velocity.y += 1
                }
            }
            when {
                moons[i].position.z > moons[j].position.z -> {
                    moons[i].velocity.z-=1
                    moons[j].velocity.z+=1
                }
                moons[i].position.z < moons[j].position.z -> {
                    moons[i].velocity.z+=1
                    moons[j].velocity.z-=1
                }
            }
        }
    }
}

fun applyVelocity(moons: List<Planet>, previousMoons: MutableList<Vector>) {
    moons.forEachIndexed { index, planet ->
        previousMoons[index] = planet.position + Vector(0,0,0)
        planet.position += planet.velocity
    }
}

fun totalEnergy(moons: List<Planet>) : Int {
    var result = 0
    moons.forEach {
        val potentialEnergy = it.position.x.absoluteValue + it.position.y.absoluteValue + it.position.z.absoluteValue
        val kineticEnergy = it.velocity.x.absoluteValue + it.velocity.y.absoluteValue + it.velocity.z.absoluteValue
        val totalEnergy = potentialEnergy * kineticEnergy
        result += totalEnergy
    }
    return result
}