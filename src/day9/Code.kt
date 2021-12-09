package day9

import io.kotest.matchers.shouldBe
import readInput

class Foo

fun main() {
    fun part1(input: List<String>): Int {
        val map = parseInput(input)
        return map.findLowestPoints().sumOf { map[it]!! + 1 }
    }

    fun part2(input: List<String>): Int {
        val map = parseInput(input)
        val lowestPoints = map.findLowestPoints()
        val basins = lowestPoints.map { map.locationsInBasin(it) }
        val sorted = basins.sortedByDescending { it.size }
        val sizes = sorted.take(3).map { it.size }
        return sizes[0] * sizes[1] * sizes[2]
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${Foo::class.java.packageName}/input_test")
    part1(testInput) shouldBe 15

    val input = readInput("${Foo::class.java.packageName}/input")
    println("part 1 solution: ${part1(input)}")

    part2(testInput) shouldBe 1134
    println("part 2 solution: ${part2(input)}")
}

fun Map<Point,Int>.locationsInBasin(p: Point): List<Point> {
    val toVisit = ArrayDeque<Point>()
    toVisit.add(p)
    val inBasin = mutableSetOf<Point>()
    while(true) {
        if(toVisit.isEmpty()) { break; }
        val point = toVisit.removeFirst()
//        println("Visiting $point")
        if (getOrDefault(point, 9) != 9) {
            inBasin.add(point)
            if (!inBasin.contains(point.up())) toVisit.add(point.up())
            if (!inBasin.contains(point.down())) toVisit.add(point.down())
            if (!inBasin.contains(point.left())) toVisit.add(point.left())
            if (!inBasin.contains(point.right())) toVisit.add(point.right())
        }
    }
    return inBasin.toList()
}

fun Map<Point, Int>.findLowestPoints(): List<Point> {
    val lowestPoints = mutableListOf<Point>()
    keys.map {
        if(isLowestOnTheBlock(it)) {
            lowestPoints.add(it)
        }
    }
    return lowestPoints
}

data class Point(val x: Int, val y: Int) {
    fun up() = Point(x, y-1)
    fun down() = Point(x, y+1)
    fun left() = Point(x-1, y)
    fun right() = Point(x+1, y)

}
fun parseInput(input: List<String>): Map<Point, Int> {
    val map = mutableMapOf<Point, Int>()
    var y = 0
    var x = 0
    input.forEach { line ->
        line.forEach {
            map[Point(x,y)] = "$it".toInt()
            x++
        }
        x=0
        y++
    }
    return map
}

fun Map<Point,Int>.isLowestOnTheBlock(p: Point): Boolean {
    val value = getOrDefault(p, 200)
    return value < getOrDefault(p.left(), 100) &&
            value < getOrDefault(p.right(), 100) &&
            value < getOrDefault(p.up(), 100) &&
            value < getOrDefault(p.down(), 100)
}
