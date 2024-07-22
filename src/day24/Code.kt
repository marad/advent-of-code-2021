package day24

import io.kotest.matchers.shouldBe
import readInput

class Foo

fun main() {
    fun part1(code: List<String>): Long {
//        val inputs = "12345678901234"
//        checkModel(inputs)
//        val printer = Printer()
//        val alu = ALU(inputs)
//        code.forEach {
//            alu.tick(it)
//            var printed = printer.tick(it)
//            if (printed.contains("input"))  {
//                printed += "("
//                printed += alu.lastInput()
//                printed += ")"
//                println()
//            }
//            printed = printed.padEnd(20)
//
//            print(printed)
//            println("\t${alu.registers.toList()}")
//        }

        val start = 10000000000000L
        val end   = 99999999999999L
        var largest = 0L
        for(inputs in start..end) {
            if (inputs % 10000L == 0L) {
                print("\rChecking model $inputs...")
            }
//            val registers = simulate(code, inputs.toString())
//            if (registers[varId('z')] == 0L) {
            if (checkModel(inputs.toString())) {
                if (inputs > largest) {
                    largest = inputs
                }
            }
        }

        println("Largest model number is: $largest")

        return largest
//        return 123
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${Foo::class.java.packageName}/input_test")
//    part1(testInput) shouldBe 0
//    println("part 1 test ok")

    val input = readInput("${Foo::class.java.packageName}/input")
    println("part 1 solution: ${part1(input)}")

    part2(testInput) shouldBe 0
    println("part 2 test ok")
    println("part 2 solution: ${part2(input)}")
}

fun checkModel(inputs: String): Boolean {
    val digits = inputs.map { it.digitToInt().toLong() }
    val array = LongArray(4)
    calc(array, digits[0], 1, 13, 3)
    calc(array, digits[1], 1, 11, 12)
    calc(array, digits[2], 1, 15, 9)
    calc(array, digits[3], 26, -6, 12)
    calc(array, digits[4], 1, 15, 2)
    calc(array, digits[5], 26, -8, 1)
    calc(array, digits[6], 26, -4, 1)
    calc(array, digits[7], 1, 15, 13)
    calc(array, digits[8], 1, 10, 1)
    calc(array, digits[9], 1, 11, 6)
    calc(array, digits[10], 26, -11, 2)
    calc(array, digits[11], 26, 0, 11)
    calc(array, digits[12], 26, -8, 10)
    calc(array, digits[13], 26, -7, 3)

//    println(array.toList())
    return array[0] == 0L
}

fun calc(array: LongArray, input: Long, p1: Long, p2: Long, p3: Long) {
    var w = array[0]
    var x = array[1]
    var y = array[2]
    var z = array[3]

    w = input
    x = z
    x %= 26
    z /= p1
    x += p2
    x = if (x==w) 1 else 0
    x = if (x==0L) 1 else 0
    y = 25
    y *= x
    y += 1
    z *= y
    y = w
    y *= p3
    y *= x
    z += y

    array[0] = w
    array[1] = x
    array[2] = y
    array[3] = z
}

fun simulate(code: List<String>, inputs: String): LongArray {
    val alu = ALU(inputs)
    code.forEach { alu.tick(it) }
    return alu.registers
}

class Printer() {
    fun tick(instruction: String): String {
        val instr = instruction.substring(0,3)
        val a = instruction[4]

        val b = if (instruction.length >= 6) instruction.substring(6) else null
        return when (instr) {
            "inp" -> "$a = input"
            "add" -> "$a += $b"
            "mul" -> {
                if (b == "0") {
                    "$a = 0"
                } else {
                    "$a *= $b"
                }
            }
            "div" -> "$a /= $b"
            "mod" -> "$a %= $b"
            "eql" -> "$a = ($a == $b) ? 1 : 0"
            else -> "???"
        }
    }
}

class ALU(private val inputs: String) {
    private var inputIndex: Int = 0
    val registers = LongArray(4)
    val w : Long get() = registers[varId('w')]
    val x : Long get() = registers[varId('x')]
    val y : Long get() = registers[varId('y')]
    val z : Long get() = registers[varId('z')]

    fun tick(instruction: String) {
        val instr = instruction.substring(0,3)
        val a = instruction[4]
        when (instr) {
            "inp" -> setVar(a, readInput())
            "add" -> {
                val b = readB(instruction)
                setVar(a, getVar(a) + b.getValue(this))
            }
            "mul" -> {
                val b = readB(instruction)
                setVar(a, getVar(a) * b.getValue(this))
            }
            "div" -> {
                val b = readB(instruction)
                setVar(a, getVar(a) / b.getValue(this))
            }
            "mod" -> {
                val b = readB(instruction)
                setVar(a, getVar(a) % b.getValue(this))
            }
            "eql" -> {
                val b = readB(instruction)
                if (getVar(a) == b.getValue(this)) {
                    setVar(a, 1)
                } else {
                    setVar(a, 0)
                }
            }
        }
    }

    private fun readInput(): Long = inputs[inputIndex++].digitToInt().toLong()
    fun lastInput(): Char = inputs[inputIndex-1]

    private fun readB(instruction: String): NameOrValue {
        val s = instruction.substring(6)
        return if (s[0] == 'w' || s[0] == 'x' || s[0] == 'y' || s[0] == 'z') {
            Name(s[0])
        } else {
            Value(s.toLong())
        }
    }

    private fun getVar(varName: Char): Long = registers[varId(varName)]
    private fun setVar(varName: Char, value: Long) { registers[varId(varName)] = value }


    sealed interface NameOrValue {
        fun getValue(alu: ALU): Long
    }

    data class Value(val innerValue: Long) : NameOrValue {
        override fun getValue(alu: ALU): Long = innerValue
    }

    data class Name(val name: Char) : NameOrValue {
        override fun getValue(alu: ALU): Long = alu.getVar(name)
    }
}

fun varId(varName: Char): Int = when(varName) {
    'w' -> 0
    'x' -> 1
    'y' -> 2
    'z' -> 3
    else -> throw RuntimeException("Unknown variable")
}
