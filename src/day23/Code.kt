package day23

import io.kotest.matchers.shouldBe
import readInput

class Foo

class Corridor {
    val slots = Array<Char>(7) { ' ' }
    val rooms = Array<Room>(4) { Room(' ', ' ', ' ', ' ')}
}

data class Room(val a: Char, val b: Char, val c: Char, val d: Char)

fun main() {
    fun part1(input: List<String>): Int {
        return 0
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${Foo::class.java.packageName}/input_test")
    part1(testInput) shouldBe 0
    println("part 1 test ok")

    val input = readInput("${Foo::class.java.packageName}/input")
    println("part 1 solution: ${part1(input)}")

    part2(testInput) shouldBe 0
    println("part 2 test ok")
    println("part 2 solution: ${part2(input)}")
}
