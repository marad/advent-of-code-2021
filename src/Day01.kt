fun main() {
    fun part1(input: List<Int>): Int {
        var count = 0
        var last = input.first()
        input.drop(1).forEach {
            if (last < it) {
                count++
            }
            last = it
        }
        return count
    }

    fun part2(input: List<Int>): Int {
        fun window(index: Int): Int {
            return input[index] + input[index+1] + input[index+2]
        }

        val windows = (0..input.size-3).map { window(it) }

        return part1(windows)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test").map { it.toInt() }
    check(part1(testInput) == 7)

    val input = readInput("Day01").map { it.toInt() }
    println(part1(input))
    println(part2(input))
}
