package day20

import io.kotest.matchers.shouldBe
import readInput

class Foo

fun main() {
    fun part1(input: List<String>): Int {
        val algo = readAlgorithm(input)
        val image = readImage(input)
        val finalImage = enhanceTimes(algo, image, 2)
        return finalImage.size
    }

    fun part2(input: List<String>): Int {
        val algo = readAlgorithm(input)
        val image = readImage(input)
        val finalImage = enhanceTimes(algo, image, 50)
        return finalImage.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${Foo::class.java.packageName}/input_test")
    part1(testInput) shouldBe 35
    println("part 1 test ok")

    val input = readInput("${Foo::class.java.packageName}/input")
    val sol1 = part1(input)
    sol1 shouldBe 5619
    println("part 1 solution: ${part1(input)}")

    part2(testInput) shouldBe 3351
    println("part 2 test ok")
    println("part 2 solution: ${part2(input)}")
}

fun enhanceTimes(algo: String, image: Set<Point>, times: Int): Set<Point> {
    var currentImage = image
    repeat(times) {
        currentImage = currentImage.enhanceImage(algo, it)
    }
    return currentImage
}

fun Set<Point>.enhanceImage(algo: String, iteration: Int): Set<Point> {
    val result = mutableSetOf<Point>()
    val around = 1L
    val minX = minX()
    val minY = minY()
    val maxX = maxX()
    val maxY = maxX()

    val startX = minX - around
    val startY = minY - around
    val endX = maxX + around
    val endY = maxY + around

    var x = startX
    var y = startY


    while(y <= endY) {
        while(x <= endX) {
            if (enhancedPixelLit(algo, x, y, minX, minY, maxX, maxY, iteration)) {
                result.add(Point(x, y))
            }
            x++;
        }
        y++;
        x = startX
    }
    return result
}

fun Set<Point>.enhancedPixelLit(algo: String, x: Long, y: Long, minX: Long, minY: Long, maxX: Long, maxY: Long, iteration: Int): Boolean {
    val flickering = algo[0] == '#'
    return algo[algoIndexAt(x, y, minX, minY, maxX, maxY, iteration, flickering)] == '#'
}

fun Set<Point>.algoIndexAt(x: Long, y: Long, minX: Long, minY: Long, maxX: Long, maxY: Long, iteration: Int, flickering: Boolean): Int {
    var index = 0
    (-1..1).forEach { yoffset ->
        (-1..1).forEach { xoffset ->
            if (isOn(x+xoffset, y+yoffset, minX, minY, maxX, maxY, iteration, flickering)) {
                index += 1
            }
            index = index shl 1
        }
    }
    index = index shr 1
    return index
}

fun Set<Point>.isOn(x: Long, y: Long, minX: Long, minY: Long, maxX: Long, maxY: Long, iteration: Int, flickering: Boolean): Boolean {
    val outsideBounds = x < minX || x > maxX || y < minY || y > maxY
    return if (outsideBounds) {
        flickering && iteration%2==1
    } else {
        contains(Point(x, y))
    }
}

fun Set<Point>.maxX(): Long = maxOf { it.x }
fun Set<Point>.maxY(): Long = maxOf { it.y }
fun Set<Point>.minX(): Long = minOf { it.x }
fun Set<Point>.minY(): Long = minOf { it.y }

fun Set<Point>.drawImage() {
    (minY()..maxY()).forEach { y ->
        (minX()..maxX()).forEach { x ->
            if (contains(Point(x,y))) {
                print('#')
            } else {
                print('.')
            }
        }
        println()
    }
}

fun readImage(input: List<String>): Set<Point> {
    val result = mutableSetOf<Point>()
    input.drop(2).forEachIndexed { y, line ->
        line.forEachIndexed { x, pixel ->
            if (pixel == '#') {
                result.add(Point(x.toLong(),y.toLong()))
            }
        }
    }
    return result
}
fun readAlgorithm(input: List<String>) = input.first()

data class Point(val x: Long, val y: Long)

