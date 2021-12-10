package day10

import io.kotest.matchers.shouldBe
import readInput

class Foo

val syntaxScores = mapOf(
    ')' to 3,
    ']' to 57,
    '}' to 1197,
    '>' to 25137,
)

val completerScores = mapOf(
    ')' to 1,
    ']' to 2,
    '}' to 3,
    '>' to 4,
)

fun main() {
    fun part1(input: List<String>): Int {
        return input.mapNotNull { illegalChar(it) }
            .sumOf { syntaxScores[it]!! }
    }

    fun part2(input: List<String>): Long {
        val incompleteLines = input.filter { illegalChar(it) == null }
        val scores = incompleteLines.map {
            val c = completion(it)
            scoreCompletion(c)
        }.sorted()

        return scores[scores.size / 2]
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${Foo::class.java.packageName}/input_test")
    part1(testInput) shouldBe 26397

    val input = readInput("${Foo::class.java.packageName}/input")
    println("part 1 solution: ${part1(input)}")

    part2(testInput) shouldBe 288957
    println("part 2 solution: ${part2(input)}")
}

fun illegalChar(input: String): Char? {
    val opStack = ArrayDeque<Char>()
    opStack.addFirst(input[0])
    input.drop(1).forEach {
        if (opStack.isEmpty()) {
            // line is incomplete, but it's cool for now
            return null
        }
        if(it.isOpening()) {
            opStack.addFirst(it)
        } else {
            val lastOpening = opStack.first()
            if(!lastOpening.isMatchingClosing(it)) {
                // here, we found the offender!
                return it
            } else {
                opStack.removeFirst()
            }
        }
    }
    return null // we aint found shit
    // https://www.youtube.com/watch?v=g3iFJpGJiug
}

fun completion(input: String): String {
    val opStack = ArrayDeque<Char>()
    input.forEach {
        if(it.isOpening()) {
            opStack.addFirst(it)
        } else {
            opStack.removeFirst()
        }
    }
    return opStack.map { it.toClosing() }.toList().joinToString("")
}

fun scoreCompletion(completion: String): Long {
    var score = 0L
    completion.forEach {
        score = score * 5 + completerScores[it]!!
    }
    return score
}

fun Char.isOpening() = this in listOf('(', '[', '{', '<')
fun Char.isMatchingClosing(other: Char): Boolean = when(this) {
    '(' -> other == ')'
    '[' -> other == ']'
    '{' -> other == '}'
    '<' -> other == '>'
    else -> false
}

fun Char.toClosing() = when(this) {
    '(' -> ')'
    '[' -> ']'
    '{' -> '}'
    '<' -> '>'
    else -> this
}