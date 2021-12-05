package day5

import readInput
import kotlin.math.max
import kotlin.math.min

fun main() {
    fun part1(input: List<String>): Int {
        val map = mutableMapOf<Point, Int>()
        input.flatMap { makePoints(it) }
            .forEach {
                val value = map.getOrDefault(it, 0)
                map[it] = value + 1
            }
        return map.filter { it.value >= 2 }.count()
    }

    fun part2(input: List<String>): Int {
        // same as part1, change was made in `makePoints` func (else branch)
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day5/input_test")
    check(part1(testInput) == 12)

    val input = readInput("day5/input")
    println(part1(input))
    println(part2(input))
}

data class Point(val x: Int, val y: Int)

fun makePoints(lineSpec: String): List<Point> {
    val (aSpec, bSpec) = lineSpec.split(" -> ")
    val a = makePoint(aSpec)
    val b = makePoint(bSpec)

    return if (a.x == b.x) {
        // vertical line
        val (start, end) = if(a.y < b.y) listOf(a, b) else listOf(b, a)
        (start.y..end.y).map {
            Point(a.x, it)
        }
    } else if (a.y == b.y) {
        // horizontal line
        val (start, end) = if(a.x < b.x) listOf(a, b) else listOf(b, a)
        (start.x..end.x).map {
            Point(it, a.y)
        }
    } else {
        val xDir = if (a.x < b.x) 1 else -1
        val yDir = if (a.y < b.y) 1 else -1
        val xStart = min(a.x, b.x)
        val xEnd = max(a.x, b.x)
        val yStart = min(a.y, b.y)
        val yEnd = max(a.y, b.y)

        val xs = (xStart..xEnd)
        val ys = (yStart..yEnd)

        val xs2 = if (xDir == -1) xs.reversed() else xs
        val ys2 = if (yDir == -1) ys.reversed() else ys

        return xs2.zip(ys2).map { Point(it.first, it.second) }
    }
}

fun makePoint(pointSpec: String): Point {
    val (x, y) = pointSpec.trim().split(",")
    return Point(x.toInt(), y.toInt())
}