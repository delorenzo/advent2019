import java.io.File
import java.time.temporal.TemporalAmount
import javax.print.attribute.standard.OrientationRequested
import kotlin.math.roundToInt

fun main() {
    val answer = fuel("src/input/day14-input.txt")
    println(answer)
    println(1000000000000/answer)
}

private data class Ingredient(val amount: Int, val type: String)
private data class Recipe(val input: List<Ingredient>, val output: Ingredient)

fun  fuel(file: String) : Int {
    val recipes = mutableListOf<Recipe>()
    val regex = Regex("([0-9]+) (FUEL|ORE|[A-Z]+),? ?")
    File(file).forEachLine{
        val inputs = mutableListOf<Ingredient>()
        val (inputString, outputString) = it.trim().split(" => ")
        inputString.split(",").map { string -> regex.matchEntire(string.trim()) }.map {result -> result!!.groupValues }.map { groups-> inputs.add(Ingredient(groups[1].toInt(), groups[2])) }

        val outputResult = regex.matchEntire(outputString)
        val output = Ingredient(outputResult!!.groupValues[1].toInt(), outputResult.groupValues[2])

        recipes.add(Recipe(inputs, output))
    }
    val extras = mutableMapOf<String, Int>()
    val costs = mutableMapOf<String, Cost>()
    var totalCost = minCost("FUEL", extras, 1, recipes, costs)
    return totalCost
}

private data class Cost(val realCost: Int, val relativeCost: Int, val producedAmount: Int)

val myLock = Any()
private fun minCost(type: String, extras: MutableMap<String, Int>, amount: Int, recipes: List<Recipe>, costs: MutableMap<String, Cost>) : Int {
    if (type == "ORE") return 1 * amount
    synchronized(myLock) {
        if (extras.getOrDefault(type, 0) >= amount) {
            extras[type] = extras[type]!! - amount
            return 0
        }
    }
    val recipe = recipes.first{ it.output.type == type }
    synchronized(myLock) {
        val onHand = extras.getOrDefault(type, 0)
        var amountNeeded = amount
        if (onHand > 0) {
            amountNeeded -= onHand
            extras[type] = 0
        }
        var totalAmount = 0
        var total = 0
        while (totalAmount < amountNeeded) {
            val cost = recipe.input.sumBy { minCost(it.type, extras, it.amount, recipes, costs) }
            val relativeCost = (cost / recipe.output.amount)
            val minForAmount = Cost(cost, relativeCost, recipe.output.amount)
            costs[type] = minForAmount
            totalAmount += minForAmount.producedAmount
            total += cost
        }
        if (totalAmount > amountNeeded) {
            extras[type] = (extras.getOrDefault(type, 0) + totalAmount - amountNeeded)
        }
        return total
    }
}