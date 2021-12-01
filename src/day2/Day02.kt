package day2

import readInput

fun main() {
    fun part1(input: List<Int>): Int {
        return 0
    }

    fun part2(input: List<Int>): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day2/input_test").map { it.toInt() }
    check(part1(testInput) == 0)

    val input = readInput("day2/input").map { it.toInt() }
    println(part1(input))
    println(part2(input))
}
