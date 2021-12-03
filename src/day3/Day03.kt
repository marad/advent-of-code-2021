package day3

import readInput

fun main() {
    fun part1(input: List<String>): Int {
        val gammaRateSb = StringBuilder()
        val cols = input[0].length-1
        (0..cols).forEach { index ->
            gammaRateSb.append(mostCommon(input, index))
        }

        val gammaRate = gammaRateSb.toString()
        val epsilonRate = gammaRate.map {
            it.other()
        }.toCharArray().concatToString()

        val gr = Integer.parseInt(gammaRate, 2)
        val er = Integer.parseInt(epsilonRate, 2)

        return gr * er
    }

    fun part2(input: List<String>): Int {
        val or = oxygenRating(input)
        val sr = scrubberRating(input)
        return or * sr
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day3/input_test")
    check(part2(testInput) == 230)

    val input = readInput("day3/input")
    println(part1(input))
    println(part2(input))
}

fun oxygenRating(input: List<String>): Int {
    val num = filterByCriteria(input, ::mostCommon).single()
    return Integer.parseInt(num, 2)
}
fun scrubberRating(input: List<String>): Int {
    val num = filterByCriteria(input, ::leastCommon).single()
    return Integer.parseInt(num, 2)
}

fun filterByCriteria(input: List<String>, criteriaFunc: (List<String>, Int) -> Char): List<String> {
    var results = input
    var index = 0
    while(results.size > 1) {
        val criteriaBit = criteriaFunc(results, index)
        results = results.filterByBit(index, criteriaBit)
        index++
    }
    return results
}

fun List<String>.filterByBit(bitIndex: Int, criteriaBit: Char): List<String> =
    filter { it[bitIndex] == criteriaBit }

fun leastCommon(input: List<String>, index: Int): Char = mostCommon(input, index).other()

fun mostCommon(input: List<String>, index: Int): Char {
    val oneCount = bitsAt(input, index).count { it == '1' }
    val zeroCount = input.size - oneCount
    return if (oneCount == zeroCount) {
        '1'
    } else {
        if (oneCount > zeroCount) '1' else '0'
    }
}

fun bitsAt(input: List<String>, index: Int): List<Char> = input.map { it[index] }
fun Char.other() = if (this == '1') '0' else '1'