package day15

import io.kotest.matchers.shouldBe
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath
import org.jgrapht.graph.DefaultDirectedWeightedGraph
import org.jgrapht.graph.DefaultEdge
import readInput
import java.lang.NullPointerException

class Foo

fun main() {
    fun part1(input: List<String>): Int {
        val map = readMap(input)
        return getShortestPathWeight(map, input[0].length, input.size)
    }

    fun part2(input: List<String>): Int {
        val map = readMap(input)
        return getShortestPathWeight(map, input[0].length*5, input.size*5)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${Foo::class.java.packageName}/input_test")
    part1(testInput) shouldBe 40

    val input = readInput("${Foo::class.java.packageName}/input")
    println("part 1 solution: ${part1(input)}")

    part2(testInput) shouldBe 315
    println("part 2 solution: ${part2(input)}")
}

data class Point(val x: Int, val y: Int) {
    fun up() = Point(x, y-1)
    fun down() = Point(x, y+1)
    fun left() = Point(x-1, y)
    fun right() = Point(x+1, y)
}

fun Map<Point, Int>.getPoint(point: Point, sizeX: Int, sizeY: Int): Int? {
    val tileX = point.x / sizeX
    val tileY = point.y / sizeY
    val x = point.x % sizeX
    val y = point.y % sizeY
    return get(Point(x,y))?.let {
        val value = (it + tileX + tileY)
        if (value > 9) {
            return value - 9
        } else {
            return value
        }
    }
}

fun getShortestPathWeight(map: Map<Point, Int>, dimX: Int, dimY: Int): Int {
    val graph = DefaultDirectedWeightedGraph<Point, DefaultEdge>(DefaultEdge::class.java)

    val mapMaxX = map.keys.maxOf { it.x } + 1
    val mapMaxY = map.keys.maxOf { it.y } + 1

    repeat(dimY) { y ->
        repeat(dimX) { x ->

            val point = Point(x,y)

            graph.addVertex(point)
            val right = point.right()
            val canMoveRight = right.x < dimX
            if (canMoveRight) {
                if (!graph.containsVertex(right)) graph.addVertex(right)
                val edge = graph.addEdge(point, right)
                graph.setEdgeWeight(edge, map.getPoint(right, mapMaxX, mapMaxY)!!.toDouble())
            }

            val down = point.down()
            val canMoveDown = down.y < dimY
            if (canMoveDown) {
                if (!graph.containsVertex(down)) graph.addVertex(down)
                val edge = graph.addEdge(point, down)
                graph.setEdgeWeight(edge, map.getPoint(down, mapMaxX, mapMaxY)!!.toDouble())
            }

            val up = point.up()
            val canMoveUp = up.y >= 0
            if (canMoveUp) {
                if (!graph.containsVertex(up)) graph.addVertex(up)
                val edge = graph.addEdge(point, up)
                graph.setEdgeWeight(edge, map.getPoint(up, mapMaxX, mapMaxY)!!.toDouble())
            }

            val left = point.left()
            val canMoveLeft = left.x >= 0
            if (canMoveLeft) {
                if (!graph.containsVertex(left)) graph.addVertex(left)
                val edge = graph.addEdge(point, left)
                graph.setEdgeWeight(edge, map.getPoint(left, mapMaxX, mapMaxY)!!.toDouble())
            }
        }
    }

    val algo = BellmanFordShortestPath(graph)

    val start = Point(0,0)
    return algo.getPathWeight(start, Point(dimX-1, dimY-1)).toInt()
}

fun readMap(input: List<String>): Map<Point, Int> {
    val map = mutableMapOf<Point, Int>()
    var x = 0
    var y = 0
    input.forEach {  line ->
        line.forEach {
            val value = it.digitToInt()
            val point = Point(x, y)
            map[point] = value
            x += 1
        }
        x = 0
        y += 1
    }
    return map
}