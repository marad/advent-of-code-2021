package day4

import readInput

fun main() {
    fun part1(input: List<String>): Int {
        val inputs = readInputs(input[0])
        val boards = readBoards(input.drop(1))

        inputs.forEach { number ->
            boards.forEach {
                it.markNumber(number)
                if (it.isWinning()) {
                    return score(number, it)
                }
            }
        }

        return 0
    }

    fun part2(input: List<String>): Int {
        val inputs = readInputs(input[0])
        val boards = readBoards(input.drop(1))

        val scores = mutableListOf<Int>()

        inputs.forEach { number ->
            boards.forEach {
                if (!it.isWinning()) {
                    it.markNumber(number)
                    if (it.isWinning()) {
                        scores.add(score(number, it))
                    }
                }
            }
        }

        return scores.last()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day4/input_test")
    check(part2(testInput) == 1924)

    val input = readInput("day4/input")
    println(part1(input))
    println(part2(input))
}


fun score(number: Int, board: Board): Int {
    val unmarkedSum = board.getUnmarked().sum()
    return number * unmarkedSum
}

fun readInputs(numbersLine: String): List<Int> = numbersLine.split(",").map { it.toInt() }
fun readBoards(definitions: List<String>): List<Board> =
    definitions.filter { it.isNotEmpty() }
        .chunked(5)
        .map { rawDef ->
            val def = rawDef.map { it.trim().split("\\s+".toRegex()) }

            val rows = def.map { parseRowCol(it) }
            val cols = def.indices.map { column ->
                parseRowCol(def.map { it[column] })
            }
            Board(rows, cols)
        }

data class Board(val rows: List<RowCol>, val columns: List<RowCol>) {
    fun markNumber(number: Int) {
        rows.forEach { it.markNumber(number) }
        columns.forEach { it.markNumber(number) }
    }

    fun isWinning() =
        rows.any { it.allMarked() } || columns.any { it.allMarked() }

    fun getUnmarked(): Set<Int> =
        rows.flatMap { it.getUnmarked() }.toSet()
}

data class RowCol(val numbers: MutableSet<Int>, val marked: MutableSet<Int> = mutableSetOf()) {
    fun markNumber(number: Int) {
        marked.add(number)
    }

    fun allMarked(): Boolean = getUnmarked().isEmpty()
    fun getUnmarked(): Set<Int> = numbers - marked
}

fun parseRowCol(rowLine: List<String>): RowCol = RowCol(rowLine.map { it.toInt() }.toMutableSet())
