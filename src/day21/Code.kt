package day21

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import readInput
import java.lang.RuntimeException
import kotlin.math.pow

class Foo

fun main() {
    println(Fraction.zero + Fraction.one)
    fun part1(input: List<String>): Int {
        val die = DeterministicDie()
        val game = Game(
            startingPosition(input[0]),
            startingPosition(input[1]),
            winningScore = 1000,
            rollPerTurnCount = 3,
        )
        game.simulate(die)
        return (game.loosingPlayerScore() * die.rollCount()).toInt()
    }

    fun part2(input: List<String>): ULong {
        val pos1 = startingPosition(input[0])
        val pos2 = startingPosition(input[1])

        val game = Game(pos1, pos2, winningScore = 21, rollPerTurnCount = 1)
        val winProbabilities = countWinningProbabilities(game)

        println(winProbabilities)

        if (winProbabilities.player1 > winProbabilities.player2) {
            return winProbabilities.player1
        } else {
            return winProbabilities.player2
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${Foo::class.java.packageName}/input_test")
    part1(testInput) shouldBe 739785
    println("part 1 test ok")

    val input = readInput("${Foo::class.java.packageName}/input")
    println("part 1 solution: ${part1(input)}")

    part2(testInput) shouldBe 444356092776315UL
    println("part 2 test ok")
    println("part 2 solution: ${part2(input)}")
}

/****
 *
 */

fun countWinningProbabilities(game: Game): WinningUniverses {
    return (3..9).map { calcResults(game.clone(), it, emptyList()) }.sum()
}

fun calcResults(game: Game, currentRoll: Int, rollsSoFar: List<Int>): WinningUniverses {
    game.tick(ConstantDie(currentRoll))
    val newRolls = rollsSoFar + currentRoll
//    println("Rolls: $newRolls")
    return if (game.isDone()) {
        when(game.winningPlayer()) {
            1 -> WinningUniverses(newRolls.universes(), 0UL)
            2 -> WinningUniverses(0UL, newRolls.universes())
            else -> throw RuntimeException("There are no other players!")
        }
    } else {
        return (3..9).map {
            calcResults(game.clone(), it, newRolls)
        }.sum()
    }
}

fun startingPosition(line: String): Int = line.substring(28).toInt()

data class WinningUniverses(
    val player1: ULong,
    val player2: ULong,
) {
    operator fun plus(other: WinningUniverses): WinningUniverses {
        return WinningUniverses(
            player1 = player1 + other.player1,
            player2 = player2 + other.player2,
        )
    }
}

data class WinProbability(
    val player1: Fraction,
    val player2: Fraction
) {
    operator fun plus(other: WinProbability): WinProbability {
        return WinProbability(
            player1 = player1 + other.player1,
            player2 = player2 + other.player2,
        )
    }
}

fun List<WinningUniverses>.sum() = reduce { a, b -> a + b }

data class GameResult(
    val probability: Fraction,
    val winningPlayer: Int
)

class Game(var player1Position: Int,
           var player2Position: Int,
           val winningScore: Int,
           val rollPerTurnCount: Int
) {
    var turns: Int = 0
    var player1Turn = true
    var player1Score: Long = 0
    var player2Score: Long = 0

    fun clone(): Game {
        val game = Game(player1Position, player2Position, winningScore, rollPerTurnCount)
        game.turns = turns
        game.player1Turn = player1Turn
        game.player1Score = player1Score
        game.player2Score = player2Score
        return game
    }

    fun winningPlayer(): Int = if (player1Score > player2Score) 1 else 2

    fun loosingPlayerScore() = Math.min(player1Score, player2Score)

    fun isDone(): Boolean = player1Score >= winningScore || player2Score >= winningScore

    fun simulate(die: Die) {
        while(!isDone()) {
            tick(die)
        }
    }

    fun tick(die: Die) {
        var roll = 0
        repeat(rollPerTurnCount) {
            roll += die.roll()
        }

        if (player1Turn) {
            player1Position += roll
            while(player1Position > 10)
                player1Position -= 10
            player1Score += player1Position
        } else {
            player2Position += roll
            while(player2Position > 10)
                player2Position -= 10
            player2Score += player2Position
        }
        player1Turn = !player1Turn
        turns += 1
    }

    fun debug() {
        println("turn: $turns | P1: s $player1Score, p $player1Position, P2: s $player2Score, p $player2Position")
    }
}

interface Die {
    fun roll(): Int
}

class DeterministicDie : Die {
    private var nextRoll = 1;
    private var rollCount = 0L
    override fun roll(): Int {
        return nextRoll.also {
            rollCount += 1
            nextRoll += 1
            if (nextRoll > 100)
                nextRoll -= 100
        }
    }

    fun rollCount(): Long {
        return rollCount
    }
}

class PredefinedDie(private val rollSequence: ByteArray) : Die {
    private var index = 0
    override fun roll(): Int {
        if (index >= rollSequence.size) {
            throw RuntimeException("Well, this game isn't finished yet but I don't know the next roll for sequence $rollSequence!")
        }
        return rollSequence[index++].toInt()
    }

}

class ConstantDie(val value: Int) : Die {
    override fun roll(): Int = value
}

fun generateListOfAllPossibleDiceThrowSequences(sequenceLength: Int): Sequence<ByteArray> {
    val possibleCombinations = 7.0.pow(sequenceLength).toInt()
    val numberSeq = generateSequence(1) { it + 1}

    return numberSeq.map { it ->
        it.toString(7)
            .padStart(sequenceLength, '0').map {
                when (it) {
                    '0' -> 3
                    '1' -> 4
                    '2' -> 5
                    '3' -> 6
                    '4' -> 7
                    '5' -> 8
                    '6' -> 9
                    else -> throw RuntimeException("How?!")
                }
            }
            .map { it.toByte() }
            .toByteArray()
    }.take(possibleCombinations)
}


fun List<Int>.universes(): ULong {
    return map(::rollUniverses).reduce { a, b -> a * b }
}

fun rollUniverses(roll: Int): ULong = when(roll) {
    3 -> 1UL
    4 -> 3UL
    5 -> 6UL
    6 -> 7UL
    7 -> 6UL
    8 -> 3UL
    9 -> 1UL
    else -> throw RuntimeException("Roll $roll should not have happend.")
}

fun List<Int>.probability(): Fraction {
    return map(::rollProbability).reduce { a, b -> a * b}
}

fun rollProbability(roll: Int): Fraction {
    return when(roll) {
        3 -> Fraction(1, 27)
        4 -> Fraction(3, 27)
        5 -> Fraction(6, 27)
        6 -> Fraction(7, 27)
        7 -> Fraction(6, 27)
        8 -> Fraction(3, 27)
        9 -> Fraction(1, 27)
        else -> throw RuntimeException("Roll $roll should not have happend.")
    }
}

data class Fraction(private var dividend: Long, private var divisor: Long) {
    fun dividend() = dividend
    fun divisor() = divisor
    companion object {
        val zero = Fraction(0,1)
        val one = Fraction(1,1)
    }

    init {
        simplify()
    }
    operator fun plus(other: Fraction): Fraction {
        val newFrac = Fraction(
            dividend = dividend * other.divisor + other.dividend * divisor,
            divisor = divisor * other.divisor
        )
        return newFrac
    }

    operator fun times(other: Fraction): Fraction {
        val newFrac = Fraction(
            dividend = dividend * other.dividend,
            divisor = divisor * other.divisor
        )
        return newFrac
    }

    private fun simplify() {
        if (dividend == 0L) return
        val gcd = gcd(dividend, divisor)
        dividend /= gcd
        divisor /= gcd
    }

    override fun toString(): String = "($dividend/$divisor)"
}

// lowest common multiply
fun lcm(a: Long, b: Long): Long {
    return (a*b) / gcd(a,b)
}

// greates common divisor
fun gcd(a: Long, b: Long): Long {
    var x = a
    var y = b
    while(y != 0L) {
        val rem = x % y
        x = y
        y = rem
    }
    return x
}

suspend fun <A, B> Array<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
    map { async { f(it) } }.awaitAll()
}