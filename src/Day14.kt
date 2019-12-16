import java.io.File
import java.time.temporal.TemporalAmount
import javax.print.attribute.standard.OrientationRequested
import kotlin.math.roundToInt

fun main() {
    val answer = fuel("src/input/day14-input.txt")
    println(answer)
}
//(843220, 2169535)
private data class Ingredient(val amount: Long, val type: String)
private data class Recipe(val input: List<Ingredient>, val output: Ingredient)

fun fuel(file: String) : Pair<Long, Long> {
    val recipes = mutableListOf<Recipe>()
    val regex = Regex("([0-9]+) (FUEL|ORE|[A-Z]+),? ?")
    File(file).forEachLine{
        val inputs = mutableListOf<Ingredient>()
        val (inputString, outputString) = it.trim().split(" => ")
        inputString.split(",").map { string -> regex.matchEntire(string.trim()) }.map {result -> result!!.groupValues }.map { groups-> inputs.add(Ingredient(groups[1].toLong(), groups[2])) }

        val outputResult = regex.matchEntire(outputString)
        val output = Ingredient(outputResult!!.groupValues[1].toLong(), outputResult.groupValues[2])

        recipes.add(Recipe(inputs, output))
    }
    val extras = mutableMapOf<String, Long>()
    var totalCost = minCost("FUEL", extras, 1, recipes)
    var fuel = maxFuel(recipes)
    return totalCost to fuel
}
fun MutableMap<String, Long>.times(num: Long) {
    this.forEach{ (key, value) ->
        this[key] = value * num
    }
}

val myLock = Any()

private fun maxFuel(recipes: List<Recipe>): Long {
    val extras = mutableMapOf<String, Long>()
    var oreLeft = 1000000000000
    var fuel = 0L
    while (oreLeft > 0) {
        var totalCost = minCost("FUEL", extras, 1, recipes)
        oreLeft -= totalCost
        fuel++
    }
    return fuel-1
}

private fun minCost(type: String, extras: MutableMap<String, Long>, amount: Long, recipes: List<Recipe>) : Long {
    if (type == "ORE") return 1L * amount
    synchronized(myLock) {
        if (extras.getOrDefault(type, 0) >= amount) {
            extras[type] = extras[type]!! - amount
            return 0
        }
    }
    val recipe = recipes.first{ it.output.type == type }
    synchronized(myLock) {
        val onHand = extras.getOrDefault(type, 0L)
        var amountNeeded = amount
        if (onHand > 0) {
            amountNeeded -= onHand
            extras[type] = 0L
        }
        var totalAmount = 0L
        var total = 0L
        while (totalAmount < amountNeeded) {
            var cost = 0L
            recipe.input.map { cost += minCost(it.type, extras, it.amount, recipes) }
            totalAmount += recipe.output.amount
            total += cost
        }
        if (totalAmount > amountNeeded) {
            extras[type] = (extras.getOrDefault(type, 0) + totalAmount - amountNeeded)
        }
        return total
    }
}