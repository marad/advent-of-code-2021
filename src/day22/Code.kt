package day22

import io.kotest.matchers.shouldBe
import readInput
import kotlin.math.max
import kotlin.math.min

class Foo

fun main() {
    fun part1(input: List<String>): Long {
        val instrs = readInput(input)
        val area = Cube(
            xRange = -50..50,
            yRange = -50..50,
            zRange = -50..50
        )
        return calculateVolume(instrs, area)
    }

    fun part2(input: List<String>): Long {
        val instrs = readInput(input)
        return calculateVolume(instrs)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${Foo::class.java.packageName}/input_test")
    part1(testInput) shouldBe 474140L
    println("part 1 test ok")

    val input = readInput("${Foo::class.java.packageName}/input")
    println("part 1 solution: ${part1(input)}")

    part2(testInput) shouldBe 2758514936282235L
    println("part 2 test ok")
    println("part 2 solution: ${part2(input)}")
}


fun calculateVolume(inputInstructions: List<Instr>, area: Cube? = null): Long {
    val instructions = inputInstructions.map { it.copy(cube = it.cube.maybeIntersect(area)) }
    var currentCube = instructions.first().cube
    var volume: Long = currentCube.volume()
    val shape = mutableListOf(instructions.first())
    var boundingBox = currentCube
    instructions.drop(1).forEach { instr ->
        currentCube = instr.cube
        val boundingBoxIntersection = boundingBox.intersect(currentCube)
        val intersectionVolume: Long =
            if (boundingBoxIntersection.isEmpty()) {
                0
            } else {
                calculateVolume(shape, boundingBoxIntersection)
            }

        if (instr.action == Action.ON) {
            volume += instr.cube.volume() - intersectionVolume
        } else if(instr.action == Action.OFF) {
            volume -= intersectionVolume
        }
        shape.add(instr)
        boundingBox = shape.map { it.cube }.findBounds()
    }
    return volume
}


fun Iterable<Cube>.findBounds(): Cube {
    var xAreaRange = 0..0
    var yAreaRange = 0..0
    var zAreaRange = 0..0

    forEach { cube ->
        xAreaRange = IntRange(min(xAreaRange.first, cube.xRange.first), max(xAreaRange.last, cube.xRange.last))
        yAreaRange = IntRange(min(yAreaRange.first, cube.yRange.first), max(yAreaRange.last, cube.yRange.last))
        zAreaRange = IntRange(min(zAreaRange.first, cube.zRange.first), max(zAreaRange.last, cube.zRange.last))
    }

    return Cube(xAreaRange, yAreaRange, zAreaRange)
}

fun readInput(instructions: List<String>) = instructions.map(::readInstruction)

fun readInstruction(line: String): Instr {
    fun parseRange(rangeString: String): IntRange {
        val (start, end) = rangeString.split("..").map { it.toInt() }
        return IntRange(start, end)
    }

    val (actionString, rangesString) = line.split(" ")
    val action = if (actionString == "on") Action.ON else Action.OFF
    val ranges = rangesString.split(",")
        .map { it.drop(2) }
        .map { parseRange(it) }

    return Instr(
        action,
        Cube(
            ranges[0],
            ranges[1],
            ranges[2]
        )
    )
}

data class Instr(val action: Action, val cube: Cube)

data class Cube(val xRange: IntRange, val yRange: IntRange, val zRange: IntRange) {
    fun volume(): Long {
        val xRowLength = (xRange.last - xRange.first + 1).toLong()
        val yRowLength = (yRange.last - yRange.first + 1).toLong()
        val zRowLength = (zRange.last - zRange.first + 1).toLong()
        val xyPlaneArea = xRowLength * yRowLength
        return xyPlaneArea * zRowLength
    }

    fun isEmpty() = xRange.isEmpty() || yRange.isEmpty() || zRange.isEmpty()

    fun intersect(other: Cube): Cube {
        return Cube(
            rangeIntersection(xRange, other.xRange),
            rangeIntersection(yRange, other.yRange),
            rangeIntersection(zRange, other.zRange),
        )
    }

    fun maybeIntersect(other: Cube?): Cube =
        if (other != null) {
            intersect(other)
        } else {
            this
        }
}

fun rangeIntersection(ai: IntRange, bi: IntRange): IntRange {
    // check if a intersect b from the left
    val (a,b) = if (ai.first < bi.first) {
        ai to bi
    } else {
        bi to ai
    }

    if (a.last >= b.first && a.last <= b.last) {
        // intersection on the left
        return IntRange(b.first, a.last)
    } else if (a.first <= b.last && a.first >= b.first) {
        // intersection from the right
        return IntRange(a.first, b.last)
    } else if (a.first >= b.first && a.last <= b.last) {
        // a is within b
        return a
    } else if (b.first >= a.first && b.last <= a.last) {
        // b is within a
        return b
    }
    return IntRange.EMPTY
}


enum class Action { ON, OFF }