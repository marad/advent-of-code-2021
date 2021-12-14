package day14

import io.kotest.matchers.shouldBe
import readInput

class Foo

fun main() {
    fun part1(input: List<String>): Long {
        val rules = readRules(input)
        var polymer = readTemplate(input)
        val counts = simulateSteps2(polymer, rules, 10)
        val mostFrequent = counts.values.maxOf { it }
        val leastFrequent = counts.values.minOf { it }
        return mostFrequent - leastFrequent
    }

    fun part2(input: List<String>): Long {
        val rules = readRules(input)
        var polymer = readTemplate(input)
        val counts = simulateSteps2(polymer, rules, 40)
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
    println("Test input ok for part 2")
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

fun simulateSteps2(input: String, rules: Map<String, Char>, maxSteps: Int): Map<Char, Long> {
    val result = mutableMapOf<Char, Long>()
    val stack = ArrayDeque<Pair<Char, Int>>()
    input.forEach { stack.addLast(it to 0) }

    var printCounter = 0

    while(stack.size >= 2) {
        val (char, lastStep) = stack.removeFirst()
        val (nextChar, _) = stack.first()
        val expansion = rules["$char$nextChar"]

        val nextStep = lastStep+1

        if (expansion != null) {
            stack.addFirst(expansion to nextStep)
        }

        stack.addFirst(char to nextStep)

        while(stack.first().second >= maxSteps) {
            val (c, _) = stack.removeFirst()
            val count = result.getOrDefault(c, 0)
            result[c] = count+1
        }

//        if (printCounter >= 1000) {
//            print("\rStack size: ${stack.size}; Result: $result")
//            print("\r$stack")
//            printCounter = 0
//        }
//        printCounter++
    }

    while(stack.isNotEmpty()) {
        val (c, _) = stack.removeFirst()
        val count = result.getOrDefault(c, 0)
        result[c] = count+1
    }

    return result
}

fun simulateSteps(input: String, rules: Map<String,Char>, steps: Int): Map<Char, Int> {
    var polymer = input
    repeat(steps) {
        polymer = polymer.expandTemplate(rules)
        println(polymer)
    }
    return polymer.groupBy { it }.mapValues { it.value.size }
}

fun String.expandTemplate(rules: Map<String, Char>): String {
    val sb = StringBuilder()
    var index = 0
    sb.append(first())
    while(index <= length-2) {
        val pair = substring(index, index+2)
        var part = rules[pair]
        if (part != null) {
            sb.append(part)
        }
        sb.append(get(index+1))
        index++
    }
    return sb.toString()
}
