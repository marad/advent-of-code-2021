package day21

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.*
import readInput
import java.lang.RuntimeException

class Foo

fun main() {
    fun part1(input: List<String>): Int {
        val die = DeterministicDie()
        val gameState = GameState(
            startingPosition(input[0]),
            startingPosition(input[1]),
        )
        simulate(gameState,
            updateState = {
                val roll = die.roll() + die.roll() + die.roll()
                updateState(it, roll)
            },
            isDone = { isDone(it, winningScore = 1000) })

        return (gameState.loosingPlayerScore() * die.rollCount()).toInt()
    }

    fun part2(input: List<String>): Long {
        val pos1 = startingPosition(input[0])
        val pos2 = startingPosition(input[1])

        val startTime = System.currentTimeMillis()
        val gameState = GameState(pos1, pos2)
        val winningUniverses = calculateWinningUniverses(gameState, winningScore = 21)

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

fun calculateWinningUniverses(initialGameState: GameState, winningScore: Int): WinningUniverses {
    fun calcResults(gameState: GameState, currentRoll: Int, universes: Long): WinningUniverses {
        updateState(gameState, currentRoll)
        val thisRollUniverses = universes * rollUniverses(currentRoll)
        return if (isDone(gameState, winningScore)) {
            when(gameState.winningPlayer()) {
                1 -> WinningUniverses(thisRollUniverses, 0L)
                2 -> WinningUniverses(0L, thisRollUniverses)
                else -> throw RuntimeException("There are no other players!")
            }
        } else {
            (3..9).map {
                calcResults(gameState.clone(), it, thisRollUniverses)
            }.sum()
        }
    }

    return runBlocking(Dispatchers.Default) {
        (3..9).pmap { calcResults(initialGameState.clone(), it, 1) }.sum()
    }
}

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

fun simulate(state: GameState, updateState: (GameState) -> Unit, isDone: (GameState) -> Boolean) {
    while(!isDone(state)) {
        updateState(state)
    }
}

fun isDone(state: GameState, winningScore: Int) =
    state.player1Score >= winningScore || state.player2Score >= winningScore

fun updateState(state: GameState, roll: Int) {
    if (state.player1Turn) {
        state.player1Position += roll
        while(state.player1Position > 10)
            state.player1Position -= 10
        state.player1Score += state.player1Position
    } else {
        state.player2Position += roll
        while(state.player2Position > 10)
            state.player2Position -= 10
        state.player2Score += state.player2Position
    }
    state.player1Turn = !state.player1Turn
    state.turns += 1
}

class GameState(var player1Position: Int,
                var player2Position: Int,
) {
    var turns: Int = 0
    var player1Turn = true
    var player1Score: Long = 0
    var player2Score: Long = 0

    fun clone(): GameState {
        val gameState = GameState(player1Position, player2Position)
        gameState.turns = turns
        gameState.player1Turn = player1Turn
        gameState.player1Score = player1Score
        gameState.player2Score = player2Score
        return gameState
    }

    fun winningPlayer(): Int = if (player1Score > player2Score) 1 else 2
    fun loosingPlayerScore() = Math.min(player1Score, player2Score)
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

class DeterministicDie {
    private var nextRoll = 1
    private var rollCount = 0L
    fun roll(): Int {
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

fun startingPosition(line: String): Int = line.substring(28).toInt()

suspend fun <A, B> Iterable<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
    map { async { f(it) } }.awaitAll()
}