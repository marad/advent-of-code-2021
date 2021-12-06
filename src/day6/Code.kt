package day6

import readInput

fun main() {
    fun part1(input: IntArray): Long {

        var fish = input
        repeat(80) {
            println(fish.toList())
            fish = fish.simulateDay()
        }

        return fish.count().toLong()
    }

    fun part2(input: IntArray): Long {
        var data = input.groupBy { it }.mapValues { it.value.count().toLong() }

        repeat(256) {
            println(data)
            data = data.simultateDay()
        }

        return data.values.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day6/input_test").first().split(",").map { it.toInt() }.toIntArray()
    println(part2(testInput))
    check(part2(testInput) == 26984457539)

    val input = readInput("day6/input").first().split(",").map { it.toInt() }.toIntArray()
    println(part1(input))
    println(part2(input))
}


fun IntArray.simulateDay(): IntArray {
    val newFish = (0 until this.count { it == 0 }).map { 8 }.toIntArray()
    val olderFish = map {
        val timer = it - 1
        if (timer < 0) 6 else timer
    }
    return olderFish.toIntArray() + newFish
}

fun Map<Int, Long>.simultateDay(): Map<Int, Long> {
    val newFishCount: Long =  getOrDefault(0, 0)
    return mapOf(
        0 to getOrDefault(1, 0),
        1 to getOrDefault(2, 0),
        2 to getOrDefault(3, 0),
        3 to getOrDefault(4, 0),
        4 to getOrDefault(5, 0),
        5 to getOrDefault(6, 0),
        6 to getOrDefault(7, 0) + getOrDefault(0, 0),
        7 to getOrDefault(8, 0),
        8 to newFishCount
    )
}