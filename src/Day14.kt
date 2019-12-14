import java.io.File
import java.time.temporal.TemporalAmount
import javax.print.attribute.standard.OrientationRequested
import kotlin.math.roundToInt

fun main() {
    println(fuel("src/input/day14-input5.txt"))
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
    return minCost("FUEL", mutableMapOf(), 1, recipes)
}

private data class Cost(val realCost: Int, val relativeCost: Int, val producedAmount: Int)

val myLock = Any()
private fun minCost(type: String, extras: MutableMap<String, Int>, amount: Int, recipes: List<Recipe>) : Int {
    if (type == "ORE") return 1 * amount
    synchronized(myLock) {
        if (extras.getOrDefault(type, 0) >= amount) {
            extras[type] = extras[type]!! - amount
            return 0
        }
    }
//    minCosts[type]?.let { list ->
//        val onHand = extras.getOrDefault(type, 0)
//        val minForAmount = list.minBy { it.realCost * (((amount-onHand).toFloat() / it.producedAmount.toFloat()).roundToInt()) }!!
//        extras[type] = onHand + minForAmount.producedAmount-amount
//        return minForAmount.realCost
//    }
    val recipe = recipes.first{ it.output.type == type }
//    val costs = mutableListOf<Cost>()
    val cost = recipe.input.sumBy { minCost(it.type, extras, it.amount, recipes) }
    val relativeCost = (cost / recipe.output.amount)
    val minForAmount = Cost(cost, relativeCost, recipe.output.amount)
//    minCosts[type] = costs
    synchronized(myLock) {
        val onHand = extras.getOrDefault(type, 0)
        //val minForAmount = costs.minBy { it.relativeCost }!!
        var amountNeeded = amount
        if (onHand > 0) {
            amountNeeded -= onHand
            extras[type] = 0
        }
        var totalAmount = 0
        var total = 0
        while (totalAmount < amountNeeded) {
            totalAmount += minForAmount.producedAmount
            total += minForAmount.realCost
        }
        if (totalAmount > amountNeeded) {
            extras[type] = (extras.getOrDefault(type, 0) + totalAmount - amountNeeded)
        }

        println("Total for $amount of $type is $total ORE")
        return total
    }
}