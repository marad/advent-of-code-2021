package day2

import readInput

fun main() {
    fun part1(input: List<String>): Int {
        val cmds = input.parseCommands()
        val forward = cmds.filterCommands("forward").mapToValue().sum()
        val up = cmds.filterCommands("up").mapToValue().sum()
        val down = cmds.filterCommands("down").mapToValue().sum()

        val horizontal = forward
        val depth = down - up

        return horizontal * depth
    }

    fun part2(input: List<String>): Int {
        var horizontal = 0
        var depth = 0
        var aim = 0
        input.parseCommands().forEach {
            when(it.dir) {
                "forward" -> {
                    horizontal += it.value
                    depth += it.value * aim
                }
                "up" -> aim -= it.value
                "down" -> aim += it.value
            }
        }
        return horizontal * depth
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day2/input_test")
    check(part2(testInput) == 900)

    val input = readInput("day2/input")
    println(part1(input))
    println(part2(input))
}

data class Command(val dir: String, val value: Int)

fun List<String>.parseCommands() = map {
    val (dir, value) = it.split(" ")
    Command(dir, value.toInt())
}

fun List<Command>.filterCommands(vararg commands: String) =
    filter { commands.contains(it.dir) }

fun List<Command>.mapToValue() = map { it.value }