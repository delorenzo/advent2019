fun main() {
    var count = 0
    for (num in 136818..685979) {
        count += check(num)
    }
    println(count)
}

fun check(num: Int) : Int {
    val numList = num.toString().toCharArray().map { it.toString().toInt() }
    val doubles = mutableMapOf<Int, Int>()
    numList.forEachIndexed { i, current ->
        if (i > 0 && current < numList[i-1]) return 0
        doubles[current] = doubles.getOrDefault(current, 0) + 1
    }
    return when(doubles.any{ it.value == 2 }) {
        true -> 1
        false -> 0
    }
}