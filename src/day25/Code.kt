package day25

import io.kotest.matchers.shouldBe
import readInput

class Foo

fun main() {
    fun part1(input: List<String>): Long {
        var map = readMap(input)
        map.print()
        repeat(4) {
           map = map.simulateStep()
           map.print()
        }
        while (true) {
            map = map.simulateStep()
            if (map.movedLast == 0) {
                return map.steps
            }
        }
        return 0
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${Foo::class.java.packageName}/input_test")
    part1(testInput) shouldBe 58L
    println("part 1 test ok")

    val input = readInput("${Foo::class.java.packageName}/input")
    println("part 1 solution: ${part1(input)}")

    part2(testInput) shouldBe 0
    println("part 2 test ok")
    println("part 2 solution: ${part2(input)}")
}

fun readMap(input: List<String>): CucumberMap {
    val result = mutableMapOf<Point, Facing>()
    input.forEachIndexed { y, line ->
        line.forEachIndexed { x, facingChar ->
            val facing = when(facingChar) {
                '.' -> null
                'v' -> Facing.Down
                '>' -> Facing.Right
                else -> TODO()
            }

            if (facing != null) {
                val point = Point(x,y)
                result[point] = facing
            }
        }
    }
    return CucumberMap(
        result,
        sizeX = input[0].length,
        sizeY = input.size
    )
}

data class CucumberMap(val map: Map<Point, Facing>, val sizeX: Int, val sizeY: Int, val steps: Long = 0L, val movedLast: Int = 0) {

    fun simulateStep(): CucumberMap {
        var moved = 0
        // moving right
        val afterMoveRight = map.map { entry ->
            val point = entry.key
            val facing = entry.value

            when(facing) {
                Facing.Right -> {
                    val right = point.rightWrapping(sizeX)
                    if (!map.containsKey(right)) {
                        moved++
                        right to facing
                    } else {
                        point to facing
                    }
                }
                else -> point to facing
            }
        }.toMap()

        val newMap = afterMoveRight.map { entry ->
            val point = entry.key
            val facing = entry.value

            when(facing) {
                Facing.Right -> point to facing
                Facing.Down -> {
                    val down = point.downWrapping(sizeY)
                    if (!afterMoveRight.containsKey(down)) {
                        moved++
                        down to facing
                    } else {
                        point to facing
                    }
                }
            }
        }.toMap()

        return copy(map = newMap, steps = steps+1, movedLast = moved)
    }

    fun print() {
        repeat(sizeY) { y ->
            repeat(sizeX) { x ->
                val facing = map[Point(x,y)]
                print(when(facing) {
                    null -> '.'
                    Facing.Down -> 'v'
                    Facing.Right -> '>'
                })
            }
            println()
        }
        println()
    }
}

enum class Facing {
    Down,
    Right
}

data class Point(val x: Int, val y: Int) {
    fun up() = Point(x, y-1)
    fun down() = Point(x, y+1)
    fun left() = Point(x-1, y)
    fun right() = Point(x+1, y)
    fun downWrapping(sizeY: Int) = Point(x, (y+1)%sizeY)
    fun rightWrapping(sizeX: Int) = Point((x+1)%sizeX, y)
}
