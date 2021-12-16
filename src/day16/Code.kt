package day16

import io.kotest.matchers.shouldBe
import readInput

class Foo

val charMapping = mapOf<Char, String>(
    '0' to "0000",
    '1' to "0001",
    '2' to "0010",
    '3' to "0011",
    '4' to "0100",
    '5' to "0101",
    '6' to "0110",
    '7' to "0111",
    '8' to "1000",
    '9' to "1001",
    'A' to "1010",
    'B' to "1011",
    'C' to "1100",
    'D' to "1101",
    'E' to "1110",
    'F' to "1111",
)

fun main() {
//    arrayOf(
//        0b1000_0000_0000_0000,
//        0b1000_0000_0000_0011,
//        0b0100_0000_0000_0001
//    ).toIntArray()
//        .getBits(30, 4)
//        .let { println("ASD: " + it.toString(2)) }

    fun part1(input: List<String>): Int {
        val packet = readPacket(input)
//        val string = packet.joinToString("") { it.toString(2).padStart(12, '0') }
        val string = input.first().toCharArray().joinToString("") { charMapping[it]!! }
        println(string)
        val parser2 = Parser(string)
        println("Packet: ${parser2.parsePacket()}")
        println("rest: ${parser2.tail()}")

        return 0
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${Foo::class.java.packageName}/input_test")
    part1(testInput) shouldBe 31

    val input = readInput("${Foo::class.java.packageName}/input")
    println("part 1 solution: ${part1(input)}")

    part2(testInput) shouldBe 0
    println("part 2 solution: ${part2(input)}")
}

fun String.version(): Int = substring(0, 3).toInt(2)
fun String.type(): Int = substring(3, 6).toInt(2)
fun String.lengthTypeId(): Int = substring(6, 7).toInt(2)
fun String.subpacketsLength(): Int = substring(7, 22).toInt(2)

sealed interface Packet {
    val version: Int
    val type: Int
}

data class Operator(override val version: Int, override val type: Int, val subpackets: List<Packet>): Packet
data class Literal(override val version: Int, val value: Int): Packet {
    override val type: Int = 4
}


class Parser(input: String) {
    private var position: Int = 0
    private var chars = input.toCharArray()

    fun parsePacket(): Packet {
        val version = parseVersion()
        val type = parseType()
        return if (type == 4) {
            parseLiteralPacket(version)
        } else {
            TODO()
        }
    }

    fun parseLiteralPacket(version: Int): Literal =
        Literal(version, parseLiteralValue())

    fun parseVersion(): Int = getNextInt(3)
    fun parseType(): Int = getNextInt(3)
    fun parseLiteralValue(): Int {
        var value = 0
        while(true) {
            val isLast = getNextChar() == '0'
            value = (value shl 4) or getNextInt(4)
            if (isLast) break;
        }
        return value
    }

    fun pos(): Int = position
    fun tail(): String =
        chars.drop(position).joinToString("")

    private fun getNextChar(): Char = chars[position++]

    private fun getNextInt(count: Int): Int = getNextBits(count).toInt(2)

    private fun getNextBits(count: Int): String {
        return chars.copyOfRange(position, position+count)
            .joinToString("")
            .also { position += count }
    }
}
















fun version(packet: IntArray): Int {
    return packet[0] shr 13
}

// type 4 is literal value
// any other is operator packet
fun type(packet: IntArray): Int {
    return (packet[0] shr 10) and 0b111
}

// 0 means that next 15 bits represent the number of bits
// in subpackets
// 1 means that next 11 bits number of subpackets
fun lengthTypeId(packet: IntArray): Int {
    return (packet[0] shr 7) and 0b1
}

// 0011100000000000
// VVVTTTILLLLLLLLL

fun contentsLength(packet: IntArray): Int {
    val head = packet[0] and 0b111111111
    val tail = (packet[1] shr 10)
    return (head shl 6) or tail
}

// 1101 0010 1111 1110    0010 1000
fun literalValue(packet: IntArray): Int {

    fun isLastGroup(nth: Int): Boolean {
        val bitPos = 6 + nth * 5
        return packet.getBits(bitPos, 1) == 0
    }

    fun getValueBits(nth: Int): Int {
        val bitPos = 6 + nth * 5
        println("Getting ${bitPos+1}...")
        return packet.getBits(bitPos+1, 4)
    }

    var value = 0.toULong()
    var index = 0
    while(true) {
        val bits = getValueBits(index)
        println("part $index: ${bits.toString(2)}")
        value = (value shl 4) or bits.toULong()
        if (isLastGroup(index)) break
        index += 1
    }

    return value.toInt()
}

fun IntArray.getBits(start: Int, length: Int): Int {
    val word = start / 16
    val startInWord = start % 16
    val end = 32 - startInWord - length
    val value = (get(word).toUInt() shl 16) or
            (getOrElse(word+1) {0}).toUInt()
    return (((value shl startInWord) shr startInWord) shr end).toInt()
}

fun readPacket(input: List<String>): IntArray {
    val line = input.first()
    val missingBytes = line.length % 4
//    return line.chunked(4).map { it.toInt(16) }.toIntArray()
    val padded = line.padEnd(line.length+missingBytes, '0')
    return padded.chunked(4).map { it.toInt(16) }.toIntArray()
}