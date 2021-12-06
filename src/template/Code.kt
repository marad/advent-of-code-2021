package template

import readInput

fun main() {
    fun part1(input: List<String>): Int {
        return 0
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("dayX/input_test")
    check(part2(testInput) == 0)

    val input = readInput("dayX/input")
    println(part1(input))
    println(part2(input))
}
