package day18

import io.kotest.matchers.shouldBe
import readInput
import kotlin.math.ceil
import kotlin.math.floor

class Foo

fun main() {
    fun part1(input: List<String>): Int {
        val d1 = parseNumber(input.first())
        input.drop(1).forEach {
            val number = parseNumber(it)
//            print("  ")
//            d1.show()
//            print("+ ")
//            number.show()
            d1.addAnotherList(number)
            reduce(d1)
//            print("= ")
//            d1.show()
//            println()
        }
        return d1.magnitude()
    }

    fun part2(input: List<String>): Int {
        val numbers = input.map { parseNumber(it) }
        var highestMagnitude = 0
        numbers.forEach { a ->
            numbers.forEach { b ->
                if (a != b) {
                    val xa = mutableListOf<Token>()
                    xa.addAll(a)
                    xa.addAnotherList(b)
                    reduce(xa)
                    val magA = xa.magnitude()
                    if (magA > highestMagnitude) highestMagnitude = magA

                    val xb = mutableListOf<Token>()
                    xb.addAll(b)
                    xb.addAnotherList(a)
                    reduce(xb)
                    val magB = xb.magnitude()
                    if (magB > highestMagnitude) highestMagnitude = magB
                }
            }
        }
        return highestMagnitude
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${Foo::class.java.packageName}/input_test")
    part1(testInput) shouldBe 4140
    println("part 1 test ok")

    val input = readInput("${Foo::class.java.packageName}/input")
    println("part 1 solution: ${part1(input)}")

    part2(testInput) shouldBe 3993
    println("part 2 test ok")
    println("part 2 solution: ${part2(input)}")
}

sealed interface Token

data class Value(val value: Int): Token {
    override fun toString(): String = value.toString()
}
object Open : Token {
    override fun toString(): String = "["
}

object Close : Token {
    override fun toString(): String = "]"
}

fun parseNumber(string: String): MutableList<Token> {
    val normalized = string.trim().replace("\\s+".toRegex(), "")
    val result = mutableListOf<Token>()
    normalized.forEach {
        when (it) {
            '[' -> result.add(Open)
            ']' -> result.add(Close)
            ',' -> {} // skip this
            else -> result.add(Value(it.digitToInt()))
        }
    }
    return result
}

fun List<Token>.depthAt(pos: Int): Int {
    val tokens = take(pos+1)
    val openings = tokens.filter { it == Open }.size
    val closings = tokens.filter { it == Close }.size
    return openings - closings
}

fun List<Token>.maxDepth(): Int {
    var maxDepth = 0
    var depth = 1
    forEach {
        when(it) {
            Open -> depth += + 1
            Close -> depth -= 1
            else -> {}
        }
        if (depth > maxDepth) {
            maxDepth = depth
        }
    }
    return maxDepth
}

fun List<Token>.findDepthIndex(desiredDepth: Int): Int? {
    var depth = 0
    forEachIndexed { index, it ->
        when(it) {
            Open -> depth += + 1
            Close -> depth -= 1
            else -> {}
        }
        if (depth == desiredDepth) {
            return index
        }
    }
    return null
}

data class Meta(val depth: Int, val index: Int, val isPair: Boolean)
fun List<Token>.withMeta(): List<Pair<Token, Meta>> {
    var depth = 0
    return mapIndexed { index, token ->
        when(token) {
            Open -> depth += + 1
            Close -> depth -= 1
            else -> {}
        }
        val isPair =
            index+3 < size
                    && this[index+1] is Value
                    && this[index+2] is Value
                    && this[index+3] is Close
        token to Meta(depth, index, isPair)
    }
}

fun List<Token>.firstValueToTheLeft(pos: Int): Pair<Token, Meta>? {
    return withMeta().take(pos)
        .lastOrNull { it.first is Value }
}

fun List<Token>.firstValueToTheRight(pos: Int): Pair<Token, Meta>? {
    return withMeta().drop(pos + 1)
        .firstOrNull { it.first is Value }
}


fun List<Token>.firstPairAtDepth(depth: Int): Pair<Int, Int>? {
    val index = withMeta()
        .firstOrNull { it.second.isPair && it.second.depth == depth }
        ?.second
        ?.index

    return if (index != null) {
        return index to index+3
    } else {
        null
    }
}

// return index of token to split
fun List<Token>.findTokenToSplit(): Int? {
    return mapIndexed { index, token -> index to token }
        .firstOrNull() {
            val token = it.second
            token is Value && token.value >= 10
        }
        ?.first
}

fun reduce(number: MutableList<Token>) {
    while(reduceStep(number)) {
        // do nothing
//        number.show()
    }
}

fun reduceStep(number: MutableList<Token>): Boolean {
    val pair = number.firstPairAtDepth(5)
    return if (pair != null) {
        // explode
        val left = number[pair.first+1] as Value
        val right = number[pair.first+2] as Value

        val ln = number.firstValueToTheLeft(pair.first)
        val rn = number.firstValueToTheRight(pair.second)
        if (ln != null) {
//            ln.value += left.value
            val lnIndex = ln.second.index
            val lnValue = ln.first as Value
            number.removeAt(lnIndex)
            number.add(lnIndex, lnValue.copy(value = lnValue.value + left.value))
        }
        if (rn != null) {
//            rn.value += right.value
            val rnIndex = rn.second.index
            val rnValue = rn.first as Value
            number.removeAt(rnIndex)
            number.add(rnIndex, rnValue.copy(value = rnValue.value + right.value))
        }
        number.removeAt(pair.first+3)
        number.removeAt(pair.first+2)
        number.removeAt(pair.first+1)
        number.removeAt(pair.first)
        number.add(pair.first, Value(0))
        true
    } else {
        // split
        val toSplit = number.findTokenToSplit()
        if (toSplit != null) {
            val value = number[toSplit] as Value
            val l = floor(value.value.toFloat() / 2).toInt()
            val r = ceil(value.value.toFloat() / 2).toInt()

            number.removeAt(toSplit)
            number.add(toSplit, Close)
            number.add(toSplit, Value(r))
            number.add(toSplit, Value(l))
            number.add(toSplit, Open)
            true
        } else {
            // nothing to do, number can't be reduced further
            false
        }
    }

}

fun List<Token>.show() {
    println(joinToString(" "))
}

fun MutableList<Token>.addAnotherList(other: List<Token>) {
    add(0, Open)
    addAll(other)
    add(Close)
}

fun MutableList<Token>.magnitude(): Int {
    var firstPair = withMeta().firstOrNull { it.second.isPair }
    while (firstPair != null) {
        val pairIndex = firstPair.second.index
        val l = (get(pairIndex+1) as Value).value
        val r = (get(pairIndex+2) as Value).value
        val mag = l*3 + r*2

        removeAt(pairIndex)
        removeAt(pairIndex)
        removeAt(pairIndex)
        removeAt(pairIndex)
        add(pairIndex, Value(mag))

        firstPair = withMeta().firstOrNull { it.second.isPair }
    }
    return (single() as Value).value
}