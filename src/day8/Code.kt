package day8

import io.kotest.matchers.shouldBe
import readInput

class Foo

fun main() {
    fun part1(input: List<String>): Int {
        var count = 0
        input.forEach { line ->
            val (patterns, outputsString) = line.split(" | ")
            val outputs = outputsString.split(" ")
            count += outputs.map { it.length }.count {
                // 8          4          7          1
                it == 7 || it == 4 || it == 3 || it == 2
            }
        }
        return count
    }

    fun part2(input: List<String>): Int {
        // TODO: fix this absolute mess
        val allDigits = input.map { line ->
            val (patternsString, outputsString) = line.split(" | ")



            val patterns = patternsString.split(" ")
            // determine segment mapping
            val x = patternsString.replace(" ", "").toCharArray().groupBy { it }
                .mapValues { it.value.size.toString()[0] }

            val x2 = x.mapValues {
                if (it.value == '6') { 'b' }
                else if (it.value == '4') { 'e' }
                else if (it.value == '9') { 'f' }
                else { it.value }
            }
            // zostały: (a, c)=8, (d, g)=7
            val mapping = x2.mapValues {
                if(it.value == '8') {
                    // a lub c (a jest w 6, c nie ma)
                    val one = patterns.findOne()
                    if (one.contains(it.key)) {
                        'c'
                    } else {
                        'a'
                    }
                } else if (it.value == '7') {
                    val four = patterns.findFour()
                    if (four.contains(it.key)) {
                        'd'
                    } else {
                        'g'
                    }
                } else { it.value }
            }
            // final mapping
//            println(mapping)

//            val mapping = mapOf(
//                'a' to segments[0], // 8 razy
//                'b' to segments[1], // 6 razy
//                'c' to segments[2], // 8 razy
//                'd' to segments[3], // 7 razy
//                'e' to segments[4], // 4 razy
//                'f' to segments[5], // 9 razy
//                'g' to segments[6], // 7 razy
//            )

            val digitMappingX = mapOf(
                "abcefg" to 0,
                "cf" to 1,
                "acdeg" to 2,
                "acdfg" to 3,
                "bcdf" to 4,
                "abdfg" to 5,
                "abdefg" to 6,
                "acf" to 7,
                "abcdefg" to 8,
                "abcdfg" to 9,
            )

            val digitMapping = digitMappingX.mapKeys { it.key.toCharArray().toSet() }

            val outputs = outputsString.split(" ")
//            println(outputs)
            val mapped = outputs.map { output ->
                output.toCharArray().map { mapping[it]!! }.joinToString("")
            }
//            println(mapped)
            val digits = mapped.map { digitMapping[it.toCharArray().toSet()]!! }
            println("Digits $digits")
            digits[0] * 1000 + digits[1] * 100 + digits[2] * 10 + digits[3]
        }
        return allDigits.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${Foo::class.java.packageName}/input_test")
    part1(testInput) shouldBe 26

    val input = readInput("${Foo::class.java.packageName}/input")
    println("part 1 solution: ${part1(input)}")

    part2(testInput) shouldBe 61229
    println("part 2 solution: ${part2(input)}")
}

fun List<String>.findOne() = single { it.length == 2 }
fun List<String>.findFour() = single { it.length == 4 }