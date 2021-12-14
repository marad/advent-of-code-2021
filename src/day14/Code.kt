package day14

import io.kotest.matchers.shouldBe
import readInput

class Foo

fun main() {
    fun part1(input: List<String>): Long {
        val rules = readRules(input)
        var polymer = readTemplate(input)
        val counts = countLetters(simulateSteps2(polymer, rules, 10), polymer.last())
        val mostFrequent = counts.values.maxOf { it }
        val leastFrequent = counts.values.minOf { it }
        return mostFrequent - leastFrequent
    }

    fun part2(input: List<String>): Long {
        val rules = readRules(input)
        var polymer = readTemplate(input)
        val counts = countLetters(simulateSteps2(polymer, rules, 40), polymer.last())
        val mostFrequent = counts.values.maxOf { it }
        val leastFrequent = counts.values.minOf { it }
        return mostFrequent - leastFrequent
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${Foo::class.java.packageName}/input_test")
    part1(testInput) shouldBe 1588

    val input = readInput("${Foo::class.java.packageName}/input")
    println("part 1 solution: ${part1(input)}")

    part2(testInput) shouldBe 2188189693529
    println("part 2 solution: ${part2(input)}")
}


fun readTemplate(input: List<String>): String {
    return input.first()
}


fun readRules(input: List<String>): Map<String, Char> {
    return input.drop(2).map {
        val (pair, insert) = it.split(" -> ")
        Pair(pair, insert[0])
    }.toMap()
}

fun simulateSteps2(input: String, rules: Map<String, Char>, maxSteps: Int): Map<String, Long> {
    var result = mutableMapOf<String, Long>()

    // initial setup
    var index = 0
    while(index < input.length - 1) {
        result[input.substring(index, index+2)] = 1
        index+=1
    }

    // simulate step $maxSteps times
    repeat(maxSteps) {
        // single step
        val nextMap = mutableMapOf<String, Long>()
        result.keys.forEach { pair ->
            val expansion = rules[pair]
            if (expansion != null) {
                val value = result[pair]!!
                val pairA = "${pair[0]}$expansion"
                val pairB = "$expansion${pair[1]}"

                val pairAValue = nextMap.getOrDefault(pairA, 0)
                val pairBValue = nextMap.getOrDefault(pairB, 0)

                nextMap[pairA] = pairAValue + value
                nextMap[pairB] = pairBValue + value
            }
        }
        result = nextMap

    }

    return result
}

fun countLetters(polymer: Map<String, Long>, lastLetter: Char): Map<Char, Long> {
    val result = mutableMapOf<Char, Long>()

    polymer.forEach {
        val count = it.value
        val charA = it.key[0]
        val charACount = result.getOrDefault(charA, 0)
        result[charA] = charACount + count
    }

    val lastCount = result.getOrDefault(lastLetter, 0)
    result[lastLetter] = lastCount + 1
    return result
}