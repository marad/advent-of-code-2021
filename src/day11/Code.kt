package day11

import io.kotest.matchers.shouldBe
import readInput

class Foo

fun main() {
    fun part1(input: List<String>): Int {
        val map = readInput(input)

        var step = 1
        var flashes = 0
        repeat(100) {
            flashes += map.simulateStep()
            step++
        }
        return flashes
    }

    fun part2(input: List<String>): Int {
        val map = readInput(input)

        var step = 0
        while(true) {
            map.simulateStep()
            step++

            if (map.all { it.value.energy == 0 }) {
                return step
            }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${Foo::class.java.packageName}/input_test")
    part1(testInput) shouldBe 1656

    val input = readInput("${Foo::class.java.packageName}/input")
    println("part 1 solution: ${part1(input)}")

    part2(testInput) shouldBe 195
    println("part 2 solution: ${part2(input)}")
}

fun Map<Point,Octopus>.simulateStep(): Int {
    forEach { it.value.increaseLevel() }

    var flashes = 0
    while(any { it.value.shouldFlash() }) {
        forEach {
            if (it.value.shouldFlash()) {
                flash(it.key)
                flashes += 1
                it.value.markFlashed()
            }
        }
    }

    forEach { it.value.resetLevelIfFlashed() }
    return flashes
}

fun Map<Point, Octopus>.flash(p: Point) {
    p.adjacents().map {
        get(it)?.increaseLevel()
    }
}

fun readInput(input: List<String>): Map<Point, Octopus> {
    val map = mutableMapOf<Point, Octopus>()
    var y = 0
    var x = 0
    input.forEach { line ->
        line.forEach {
            map[Point(x, y)] = Octopus("$it".toInt())
            x++
        }
        x=0
        y++
    }
    return map
}

data class Octopus(var energy: Int) {
    private var flashed = false

    fun increaseLevel() { energy += 1 }
    fun resetLevelIfFlashed() {
        if (energy > 9) {
            energy = 0
            flashed = false
        }
    }

    fun shouldFlash() = energy > 9 && !flashed
    fun markFlashed() { flashed = true }
}


data class Point(val x: Int, val y: Int) {
    fun up() = Point(x, y-1)
    fun down() = Point(x, y+1)
    fun left() = Point(x-1, y)
    fun right() = Point(x+1, y)
    fun adjacents() = listOf(
        up(), down(), left(), right(),
        Point(x+1, y+1), Point(x+1, y-1),
        Point(x-1, y+1), Point(x-1, y-1)
    )
}
