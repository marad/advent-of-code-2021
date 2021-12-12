package day12

import io.kotest.matchers.shouldBe
import readInput

class Foo

fun main() {
    fun part1(input: List<String>): Int {
        val graph = makeGraph(input)
        val toVisit = ArrayDeque<List<String>>()
        toVisit.addFirst(listOf("start"))
        val paths = mutableListOf<List<String>>()
        while(toVisit.isNotEmpty()) {
            val currentPath = toVisit.removeFirst()
            val visiting = currentPath.last()

            if (currentPath.canVisit(visiting)) {
                graph[visiting]!!.forEach {
                    if (it == "end") {
                        paths.add(currentPath + it)
                    } else {
                        toVisit.addLast(currentPath + it)
                    }
                }
            }
        }

        return paths.size
    }

    fun part2(input: List<String>): Int {

        val graph = makeGraph(input)
        val toVisit = ArrayDeque<List<String>>()
        toVisit.addFirst(listOf("start"))
        val paths = mutableListOf<List<String>>()
        while(toVisit.isNotEmpty()) {
            val currentPath = toVisit.removeFirst()
            val visiting = currentPath.last()

            if (currentPath.isValidPath()) {
                graph[visiting]!!.forEach {
                    if (it == "end") {
                        paths.add(currentPath + it)
                    } else {
                        toVisit.addLast(currentPath + it)
                    }
                }
            }
        }

        return paths.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${Foo::class.java.packageName}/input_test")
    part1(testInput) shouldBe 10

    val input = readInput("${Foo::class.java.packageName}/input")
    println("part 1 solution: ${part1(input)}")

    part2(testInput) shouldBe 36
    println("part 2 solution: ${part2(input)}")
}

fun List<String>.canVisit(v: String) =
    v[0].isUpperCase() || !dropLast(1).contains(v)

fun List<String>.isValidPath(): Boolean {
    val smallCaveVisitCounts = filter { it[0].isLowerCase() }.groupBy { it }.mapValues { it.value.count() }
    if (smallCaveVisitCounts.getOrDefault("start", 0) > 1) return false
    if (smallCaveVisitCounts.getOrDefault("end", 0) > 1) return false
    val countOfSmallCavesVisitedMoreThanOnce = smallCaveVisitCounts.count { it.value > 1 }
    val cavesVisitedAtMostTwice = smallCaveVisitCounts.all { it.value <= 2 }
    return countOfSmallCavesVisitedMoreThanOnce <= 1 && cavesVisitedAtMostTwice
}

fun makeGraph(input: List<String>): Map<String, List<String>> {
    val map = mutableMapOf<String, MutableList<String>>()
    input.forEach {
        val (a, b) = it.split("-")

        map.getOrPut(a) { mutableListOf() }.add(b)
        map.getOrPut(b) { mutableListOf() }.add(a)
    }
    return map
}
