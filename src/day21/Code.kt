package day21

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.*
import readInput
import java.lang.RuntimeException

class Foo

// no optimization
// test 27s
// prod 15s

// top pmap with IO dispatcher (split to 9 threads)
// test 11s
// prod  6s

// remove working on lists and pass universes in recursion
// test 1900 ms
// prod 900 ms

fun main() {
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

    fun part2(input: List<String>): Long {
        val pos1 = startingPosition(input[0])
        val pos2 = startingPosition(input[1])

        val startTime = System.currentTimeMillis()
        val game = Game(pos1, pos2, winningScore = 21, rollPerTurnCount = 1)
        val winningUniverses = countWinningProbabilities(game)

        println(winningUniverses)

        println("Found answer in ${System.currentTimeMillis()-startTime}ms")

        return if (winningUniverses.player1 > winningUniverses.player2) {
            winningUniverses.player1
        } else {
            winningUniverses.player2
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("${Foo::class.java.packageName}/input_test")
    part1(testInput) shouldBe 739785
    println("part 1 test ok")

    val input = readInput("${Foo::class.java.packageName}/input")
    println("part 1 solution: ${part1(input)}")

    part2(testInput) shouldBe 444356092776315L
    println("part 2 test ok")
    println("part 2 solution: ${part2(input)}")
}

fun countWinningProbabilities(game: Game): WinningUniverses {
    return runBlocking(Dispatchers.Default) {
        (3..9).pmap { calcResults(game.clone(), it, 1) }.sum()
    }
}

fun calcResults(game: Game, currentRoll: Int, universes: Long): WinningUniverses {
    game.tick(ConstantDie(currentRoll))
    val thisRollUniverses = universes * rollUniverses(currentRoll)
    return if (game.isDone()) {
        when(game.winningPlayer()) {
            1 -> WinningUniverses(thisRollUniverses, 0L)
            2 -> WinningUniverses(0L, thisRollUniverses)
            else -> throw RuntimeException("There are no other players!")
        }
    } else {
        (3..9).map {
            calcResults(game.clone(), it, thisRollUniverses)
        }.sum()
    }
}

fun startingPosition(line: String): Int = line.substring(28).toInt()

data class WinningUniverses(
    val player1: Long,
    val player2: Long,
) {
    operator fun plus(other: WinningUniverses): WinningUniverses {
        return WinningUniverses(
            player1 = player1 + other.player1,
            player2 = player2 + other.player2,
        )
    }
}

fun List<WinningUniverses>.sum() = reduce { a, b -> a + b }

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
}

interface Die {
    fun roll(): Int
}

class DeterministicDie : Die {
    private var nextRoll = 1
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

class ConstantDie(val value: Int) : Die {
    override fun roll(): Int = value
}

fun rollUniverses(roll: Int): Long = when(roll) {
    3 -> 1L
    4 -> 3L
    5 -> 6L
    6 -> 7L
    7 -> 6L
    8 -> 3L
    9 -> 1L
    else -> throw RuntimeException("Roll $roll should not have happend.")
}

suspend fun <A, B> Iterable<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
    map { async { f(it) } }.awaitAll()
}