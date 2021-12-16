package day16

import io.kotest.matchers.shouldBe
import readInput
import java.lang.RuntimeException

class Foo

fun main() {
    fun part1(input: List<String>): Int {
        val string = input.first().toCharArray().joinToString("") { charMapping[it]!! }
        val parser = Parser(string)
        val packet = parser.parsePacket()
        return packet.sumVersions()
    }

    fun part2(input: List<String>): Long {
        val string = input.first().toCharArray().joinToString("") { charMapping[it]!! }
        val parser = Parser(string)
        val packet = parser.parsePacket()
        println(packet.print())
        println(parser.tail())
        return packet.calc()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${Foo::class.java.packageName}/input_test")
    part1(testInput) shouldBe 20

    val input = readInput("${Foo::class.java.packageName}/input")
    println("part 1 solution: ${part1(input)}")

    part2(listOf("C200B40A82")) shouldBe 3
    part2(listOf("04005AC33890")) shouldBe 54
    part2(listOf("880086C3E88112")) shouldBe 7
    part2(listOf("CE00C43D881120")) shouldBe 9
    part2(listOf("D8005AC2A8F0")) shouldBe 1
    part2(listOf("F600BC2D8F")) shouldBe 0
    part2(listOf("9C005AC2F8F0")) shouldBe 0
    part2(listOf("9C0141080250320F1802104A08")) shouldBe 1
    println("part 2 solution: ${part2(input)}")
}

sealed interface Packet {
    val version: Int
    val type: Int
    fun sumVersions(): Int
    fun calc(): Long
    fun print(): String
}

data class Operator(override val version: Int, override val type: Int, val subpackets: List<Packet>): Packet {
    override fun sumVersions(): Int {
        return version + subpackets.sumOf { it.sumVersions() }
    }

    override fun calc(): Long {
        val subpacketValues = subpackets.map { it.calc() }
        return when(type) {
            TYPE_SUM -> subpacketValues.sum()
            TYPE_MUL -> subpacketValues.reduce { a, b -> a*b }
            TYPE_MIN -> subpacketValues.minOf { it }
            TYPE_MAX -> subpacketValues.maxOf { it }
            TYPE_GT -> if (subpacketValues[0] > subpacketValues[1]) 1 else 0
            TYPE_LT -> if (subpacketValues[0] < subpacketValues[1]) 1 else 0
            TYPE_EQ -> if (subpacketValues[0] == subpacketValues[1]) 1 else 0
            else -> throw RuntimeException("Unknown operation $type")
        }
    }

    override fun print(): String {
        val subpacketValues = subpackets.joinToString(" ") { it.print() }
        return when(type) {
            TYPE_SUM -> "(+ $subpacketValues)"
            TYPE_MUL -> "(* $subpacketValues)"
            TYPE_MIN -> "(min $subpacketValues)"
            TYPE_MAX -> "(max $subpacketValues)"
            TYPE_GT -> "(gt $subpacketValues)"
            TYPE_LT -> "(lt $subpacketValues)"
            TYPE_EQ -> "(eq $subpacketValues)"
            else -> throw RuntimeException("Unknown operation $type")
        }
    }
}

data class Literal(override val version: Int, val value: Long): Packet {
    override val type: Int = 4
    override fun sumVersions(): Int = version
    override fun calc(): Long  = value
    override fun print(): String = value.toString()
}

const val TYPE_SUM = 0
const val TYPE_MUL = 1
const val TYPE_MIN = 2
const val TYPE_MAX = 3
const val TYPE_LITERAL = 4
const val TYPE_GT = 5
const val TYPE_LT = 6
const val TYPE_EQ = 7
const val LENTYPE_BITCOUNT = 0
const val LENTYPE_SUBPACKETCOUNT = 1

class Parser(input: String) {
    private var position: Int = 0
    private var chars = input.toCharArray()

    fun hasMore() = (chars.size - position) >= 11

    fun parsePacket(): Packet {
        val version = parseVersion()
        val type = parseType()
        return if (type == TYPE_LITERAL) {
            parseLiteralPacket(version)
        } else {
            val lengthType = parseLengthTypeId()
            if (lengthType == LENTYPE_BITCOUNT) {
                val subpacketBits = getNextInt(15)
                val subParser = Parser(getNextBits(subpacketBits))
                val subpackets = mutableListOf<Packet>()
                while(subParser.hasMore()) {
                    subpackets.add(subParser.parsePacket())
                }
                return Operator(version, type, subpackets)
            } else if (lengthType == LENTYPE_SUBPACKETCOUNT) {
                val subpacketCount = getNextInt(11)
                val subpackets = mutableListOf<Packet>()
                repeat(subpacketCount) {
                    subpackets.add(parsePacket())
                }
                return Operator(version, type, subpackets)
            } else {
                throw RuntimeException("How, on earth?!")
            }
        }
    }

    fun parseLiteralPacket(version: Int): Literal =
        Literal(version, parseLiteralValue())

    fun parseVersion(): Int = getNextInt(3)
    fun parseType(): Int = getNextInt(3)
    fun parseLiteralValue(): Long {
        var value = 0L
        while(true) {
            val isLast = getNextChar() == '0'
            value = (value shl 4) or getNextInt(4).toLong()
            if (isLast) break
        }
        return value
    }
    fun parseLengthTypeId(): Int = getNextInt(1)

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

val charMapping = mapOf(
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

