package day17

import io.kotest.matchers.shouldBe
import readInput

class Foo

// test: target area: x=20..30, y=-10..-5
// prod: target area: x=57..116, y=-198..-148
// x goes to 0
// y goes to -inf
// cpu goes brrrrr

fun main() {
    fun part1(input: List<String>): Int {
        val area = readArea(input)
        val seq = findPossibleVelocities(area.yRange, ::yDamping, maxSteps = 1000, isLostCause = { pos, step ->
            pos < area.yRange.minOf { it }
        })
        return seq.last().second
    }

    fun part2(input: List<String>): Int {
        val area = readArea(input)
        val ySeq = findPossibleVelocities(area.yRange, ::yDamping, startFrom = -1000, maxSteps = 2000, isLostCause = { pos, _ ->
            pos < area.yRange.minOf { it }
        })
        val xSeq = findPossibleVelocities(area.xRange, ::xDamping, startFrom = -1000, maxSteps = 2000, isLostCause = { pos, step ->
            step > 1000
        })

        val xs = xSeq.map { it.first }.toList()
        val ys = ySeq.map { it.first }.toList()

        println("XS: $xs")
        println("YS: $ys")
        val results = mutableListOf<PosVec>()
        ys.forEach { y ->
            xs.forEach { x ->
                val vel = PosVec(x, y)
                print("Trying $vel...")
                if (simulate(vel, area)) {
                    results.add(vel)
                    print("ok")
                }
                println()
            }
        }
        return results.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${Foo::class.java.packageName}/input_test")
    part1(testInput) shouldBe 45

    val input = readInput("${Foo::class.java.packageName}/input")
    println("part 1 solution: ${part1(input)}")

    part2(testInput) shouldBe 112
    println("part 2 solution: ${part2(input)}")
}

fun findPossibleVelocities(range: IntRange, damping: (Int) -> Int, isLostCause: (Int, Int) -> Boolean, maxSteps: Int = 1000, startFrom: Int = 0): Sequence<Pair<Int, Int>> {
    return generateSequence(startFrom) { it + 1}
        .map { velocity -> velocity to simulate(velocity, range, damping, isLostCause) }
        .take(maxSteps)
        .filter { it.second != null }
        .map { it.first to it.second!! }
}

data class Area(val xRange: IntRange, val yRange: IntRange)

fun readArea(input: List<String>): Area {
    val (_, spec) = input.first().split(":")
    val (xRange, yRange) = spec.trim().split(",")
    val (xStart, xEnd) = xRange.trim().substring(2).split("..")
    val (yStart, yEnd) = yRange.trim().substring(2).split("..")

    return Area(
        xRange = IntRange(xStart.toInt(), xEnd.toInt()),
        yRange = IntRange(yStart.toInt(), yEnd.toInt())
    )
}

fun xDamping(input: Int): Int = when {
    input > 0 -> input - 1
    input < 0 -> input + 1
    else -> 0
}

fun yDamping(input: Int): Int = input - 1

fun simulate(startingVelocity: Int, expectedRange: IntRange, damping: (Int) -> Int, isLostCause: (Int, Int) -> Boolean): Int? {
    var position = 0
    var velocity = startingVelocity
    var highest = 0
    var step = 0
    while(true) {
        position += velocity
        if (highest < position)
            highest = position
        if (position in expectedRange) {
            return highest
        }
        if (isLostCause(position, step)) {
            return null
        }
        velocity = damping(velocity)
        step += 1
    }
}

data class PosVec(val x: Int, val y: Int) {
    fun plus(other: PosVec): PosVec =
        copy(x=x+other.x, y=y+other.y)
}

fun simulate(startingVelocity: PosVec, area: Area): Boolean {
    var pos = PosVec(0, 0)
    var velocity = startingVelocity
    var step = 0
    while(true) {
        pos =  pos.plus(velocity)
        if (pos.x in area.xRange && pos.y in area.yRange) {
            return true
        }
        if (step >= 1000) {
            return false
        }
        step += 1
        velocity = velocity.copy(
            x = xDamping(velocity.x),
            y = yDamping(velocity.y)
        )
    }
}