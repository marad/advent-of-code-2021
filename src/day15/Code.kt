package day15

import io.kotest.matchers.shouldBe
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath
import org.jgrapht.graph.DefaultDirectedWeightedGraph
import org.jgrapht.graph.DefaultEdge
import readInput

class Foo

fun main() {
    fun part1(input: List<String>): Int {
        val map = readMap(input)
        val end = Point(input[0].length-1, input.size-1)
        return getShortestPathWeight(map, end)
    }

    fun part2(input: List<String>): Int {
        val map = readMap(input)
        val bigMap = map.expand(input[0].length, input.size)
        val end = Point(input[0].length*5-1, input.size*5-1)

        return getShortestPathWeight(bigMap, end)
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

fun Map<Point, Int>.expand(xDim: Int, yDim: Int): Map<Point, Int> {
    var x=0
    var y=0
    val newMap = mutableMapOf<Point, Int>()
    while(y < yDim*5) {
        while(x < xDim*5) {
            newMap[Point(x,y)] = getPoint(Point(x,y), xDim, yDim)!!
            x += 1
        }
        x = 0
        y += 1
    }
    return newMap
}

fun getShortestPathWeight(map: Map<Point, Int>, endPoint: Point): Int {
    val graph = DefaultDirectedWeightedGraph<Point, DefaultEdge>(DefaultEdge::class.java)
    map.forEach {
        graph.addVertex(it.key)
        val right = it.key.right()
        val canMoveRight = map[right] != null
        if (canMoveRight) {
            if (!graph.containsVertex(right)) graph.addVertex(right)
            val edge = graph.addEdge(it.key, right)
            graph.setEdgeWeight(edge, map[right]!!.toDouble())
        }

        val down = it.key.down()
        val canMoveDown = map[down] != null
        if (canMoveDown) {
            if (!graph.containsVertex(down)) graph.addVertex(down)
            val edge = graph.addEdge(it.key, down)
            graph.setEdgeWeight(edge, map[down]!!.toDouble())
        }

        val up = it.key.up()
        val canMoveUp = map[up] != null
        if (canMoveUp) {
            if (!graph.containsVertex(up)) graph.addVertex(up)
            val edge = graph.addEdge(it.key, up)
            graph.setEdgeWeight(edge, map[up]!!.toDouble())
        }

        val left = it.key.left()
        val canMoveLeft = map[left] != null
        if (canMoveLeft) {
            if (!graph.containsVertex(left)) graph.addVertex(left)
            val edge = graph.addEdge(it.key, left)
            graph.setEdgeWeight(edge, map[left]!!.toDouble())
        }

    }

    val algo = BellmanFordShortestPath(graph)

    val start = Point(0,0)
    return algo.getPathWeight(start, endPoint).toInt()
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