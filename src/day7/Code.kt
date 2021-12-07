package day7

import readInput
import kotlin.math.absoluteValue

fun main() {
    fun solve(input: List<Int>, calcFuel: (Int, List<Int>) -> Int): Int {
        val max = input.maxOf { it }
        return (0 until max).map { calcFuel(it, input) }
            .minOf { it }
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day7/input_test").first().split(",").map { it.toInt() }
    check(solve(testInput, ::linear) == 37)

    val input = readInput("day7/input").first().split(",").map { it.toInt() }
    println("Part 1 ${solve(input, ::linear)}")

    check(solve(testInput, ::summing) == 168)
    println("Part 2 ${solve(input, ::summing)}")
}

fun linear(position: Int, input: List<Int>): Int {
    return input.map { (it-position).absoluteValue }.sum()
}

fun summing(position: Int, input: List<Int>): Int {
    return input.map {
        val diff = (it-position).absoluteValue
        (0..diff).sum()
    }.sum()
}