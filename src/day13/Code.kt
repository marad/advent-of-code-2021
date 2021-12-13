package day13

import io.kotest.matchers.shouldBe
import readInput

class Foo

fun main() {
    fun part1(input: List<String>): Int {
        val paper = readPaper(input)
        val instructions = readInstructions(input)

        val firstInstruction = instructions.first()

        return paper.map { firstInstruction.fold(it) }.toSet().size
    }

    fun part2(input: List<String>): Int {
        val paper = readPaper(input)
        val instructions = readInstructions(input)

        var folding = paper
        instructions.forEach { instruction ->
            folding = folding.map { instruction.fold(it) }.toSet()
        }
        val folded = folding

        val maxX = folded.maxOf { it.x }
        val maxY = folded.maxOf { it.y }

        println("------")

        repeat(maxY+1) { y ->
            repeat(maxX+1) { x ->
                if (folded.contains(Point(x,y))) {
                    print('█')
                } else {
                    print(' ')
                }
            }
            println()
        }

        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${Foo::class.java.packageName}/input_test")
    part1(testInput) shouldBe 17

    val input = readInput("${Foo::class.java.packageName}/input")
    println("part 1 solution: ${part1(input)}")

    part2(testInput) shouldBe 0
    println("part 2 solution: ${part2(input)}")
}

data class Point(val x: Int, val y: Int)
fun readPaper(input: List<String>): Set<Point> {
    return input.takeWhile { it.isNotBlank() }
        .map {
            val (x, y) = it.split(",")
            Point(x.toInt(),y.toInt())
        }.toSet()
}

sealed interface Instruction {
    fun fold(p: Point): Point
}
data class FoldHorizontally(val y: Int): Instruction {
    override fun fold(p: Point): Point {
        return if (p.y <= y) p
        else {
            val diff = p.y - y
            p.copy(y = y - diff)
        }
    }
}

data class FoldVertically(val x: Int): Instruction {
    override fun fold(p: Point): Point {
        return if(p.x <= x) p
        else {
            val diff = p.x - x
            p.copy(x = x - diff)
        }
    }
}

fun readInstructions(input: List<String>): List<Instruction> {
    return input.dropWhile { it.isNotBlank() }
        .drop(1)
        .map {
            val value = it.substring(13).toInt()
            return@map if (it.contains("x")) {
                FoldVertically(value)
            } else {
                FoldHorizontally(value)
            }
        }
}
