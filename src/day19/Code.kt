package day19

import io.kotest.matchers.shouldBe
import readInput
import kotlin.math.absoluteValue

class Foo

fun main() {
    fun part1(input: List<String>): Int {
        val scanners = parseInput(input)
//        println(scanners)
//        println(scanners[0])
        val x = scanners.map { scanner ->
            val relatives = mutableSetOf<Set<Int>>()
//            scanner.beacons.windowed(2) {
//                val set = makeSetFoo(it[0], it[1])
//                println(set)
//            }
//            println("-----")

            scanner.beacons.forEach { a ->
                scanner.beacons.forEach { b ->
                    relatives.add(makeSetFoo(a,b))
                }
            }
            relatives
        }

        scanners.forEachIndexed { indexA, scannerA ->
            scanners.forEachIndexed { indexB, scannerB ->
                if (indexA != indexB) {

                    val setA = x[indexA]
                    val setB = x[indexB]

                    val int = setA.intersect(setB)
                    if (int.size >= 12) {
                        println(indexA to indexB)
                    }
                }
            }
        }

//        x.forEachIndexed { indexA, A ->
//            x.forEachIndexed { indexB, B ->
//                if (indexA != indexB) {
//                    val found = mutableSetOf<Set<Int>>()
//                    A.forEach { a ->
//                        B.forEach { b ->
//                            val int = a.intersect(b)
//                            if (int.size >= 3) {
//                                found.add(int)
//                            }
//
//                        }
//                    }
//
//                    print(indexA to indexB)
//                    println(" - ${found.size}")
//                }
//            }
//        }




//        val p1 = Vector(-618,-824,-621)
//        val p2 = Vector(-537,-823,-458)
//        println(makeSetFoo(p1, p2))
//
//        val p3 = Vector(686,422,578)
//        val p4 = Vector(605,423,415)
//        println(makeSetFoo(p3, p4))
//
//        println(rotations(Vector(0, 5, 1)))
        return 0
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${Foo::class.java.packageName}/input_test")
    part1(testInput) shouldBe 79
    println("part 1 test ok")

    val input = readInput("${Foo::class.java.packageName}/input")
    println("part 1 solution: ${part1(input)}")

    part2(testInput) shouldBe 0
    println("part 2 test ok")
    println("part 2 solution: ${part2(input)}")
}

data class Vector(val x: Int, val y: Int, val z: Int)
data class Scanner(val beacons: List<Vector>)

fun makeSetFoo(a: Vector, b: Vector): Set<Int> =
    setOf(
        (a.x.absoluteValue - b.x.absoluteValue).absoluteValue,
        (a.y.absoluteValue - b.y.absoluteValue).absoluteValue,
        (a.z.absoluteValue - b.z.absoluteValue).absoluteValue,

        (a.x.absoluteValue - b.y.absoluteValue).absoluteValue,
        (a.y.absoluteValue - b.z.absoluteValue).absoluteValue,
        (a.z.absoluteValue - b.x.absoluteValue).absoluteValue,

        (a.x.absoluteValue - b.z.absoluteValue).absoluteValue,
        (a.y.absoluteValue - b.x.absoluteValue).absoluteValue,
        (a.z.absoluteValue - b.y.absoluteValue).absoluteValue,
    )

fun parseInput(input: List<String>): List<Scanner> {
    val scanners = mutableListOf<Scanner>()
    var beacons = mutableListOf<Vector>()
    input.forEach {
        if (!it.isBlank()) {
            if (it.startsWith("---")) {
                scanners.add(Scanner(beacons))
                beacons = mutableListOf()
            } else {
                val (x,y,z) = it.split(",")
                beacons.add(Vector(x.toInt(), y.toInt(), z.toInt()))
            }
        }
    }
    return scanners.drop(1)
}

fun rotations(vec: Vector): List<Vector> {
    val forward = Vector(vec.x, vec.y, vec.z)
    val up = Vector(vec.x, vec.z, vec.y)
    val right = Vector(vec.z, vec.x, vec.y)
    return listOf(
        // rotate forward around y
        Vector(forward.x, forward.y, forward.z),
        Vector(forward.z, forward.y, forward.x),
        Vector(forward.x, forward.y, -forward.z),
        Vector(-forward.z, forward.y, forward.x),
        // and again but upside down
        Vector(forward.x, -forward.y, forward.z),
        Vector(forward.z, -forward.y, forward.x),
        Vector(forward.x, -forward.y, -forward.z),
        Vector(-forward.z, -forward.y, forward.x),

        // rotate up by x
        Vector(up.x, up.y, up.z),
        Vector(up.x, up.z, up.y),
        Vector(up.x, up.y, -up.z),
        Vector(up.x, -up.z, up.y),
        // and again but "upside down" (wyth rotation axis pointed the other way)
        Vector(-up.x, up.y, up.z),
        Vector(-up.x, up.z, up.y),
        Vector(-up.x, up.y, -up.z),
        Vector(-up.x, -up.z, up.y),

        // rotate right by z
        Vector(right.x, right.y, right.z),
        Vector(right.y, right.x, right.z),
        Vector(right.x, -right.y, right.z),
        Vector(-right.y, right.x, right.z),
        // and again upside down
        Vector(right.x, right.y, -right.z),
        Vector(right.y, right.x, -right.z),
        Vector(right.x, -right.y, -right.z),
        Vector(-right.y, right.x, -right.z),
    )
}