package pl.jbialkowski13.nqueens.game

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class GameStateMachineImplTest {

    private object FakeConflictsResolver : ConflictsResolver {

        var conflicts: Set<Position> = emptySet()

        override fun resolve(queens: Set<Position>, position: Position): Set<Position> {
            return conflicts
        }
    }

    private val gameStateMachine = GameStateMachineImpl(
        boardSize = 4,
        conflictsResolver = FakeConflictsResolver
    )

    @Test
    fun `has correct initial game state`() = runTest {
        val expected = GameState(
            boardSize = 4,
            queens = emptySet(),
            conflictingPositions = emptySet(),
            solved = false
        )

        gameStateMachine.state.test {
            assertThat(awaitItem()).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `places queen on empty board`() = runTest {
        val position = Position(0, 0)

        gameStateMachine.onQueenPlaced(position)

        val expected = GameState(
            boardSize = 4,
            queens = setOf(position),
            conflictingPositions = emptySet(),
            solved = false
        )

        gameStateMachine.state.test {
            assertThat(awaitItem()).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `removes queen from board`() = runTest {
        val position = Position(0, 0)

        gameStateMachine.onQueenPlaced(position)
        gameStateMachine.onQueenPlaced(position)

        val expected = GameState(
            boardSize = 4,
            queens = emptySet(),
            conflictingPositions = emptySet(),
            solved = false
        )

        gameStateMachine.state.test {
            assertThat(awaitItem()).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `does not place queen when there are conflicts`() = runTest {
        val position = Position(0, 0)
        FakeConflictsResolver.conflicts = setOf(Position(1, 1))

        gameStateMachine.onQueenPlaced(position)

        val expected = GameState(
            boardSize = 4,
            queens = emptySet(),
            conflictingPositions = setOf(Position(1, 1)),
            solved = false
        )

        gameStateMachine.state.test {
            assertThat(awaitItem()).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `places queen when there are no conflicts`() = runTest {
        val position = Position(0, 0)
        FakeConflictsResolver.conflicts = emptySet()

        gameStateMachine.onQueenPlaced(position)

        val expected = GameState(
            boardSize = 4,
            queens = setOf(position),
            conflictingPositions = emptySet(),
            solved = false
        )

        gameStateMachine.state.test {
            assertThat(awaitItem()).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sets solved state when all queens are placed`() = runTest {
        val position1 = Position(0, 0)
        val position2 = Position(1, 1)
        val position3 = Position(2, 2)
        val position4 = Position(3, 3)
        FakeConflictsResolver.conflicts = emptySet()

        gameStateMachine.onQueenPlaced(position1)
        gameStateMachine.onQueenPlaced(position2)
        gameStateMachine.onQueenPlaced(position3)
        gameStateMachine.onQueenPlaced(position4)

        val expected = GameState(
            boardSize = 4,
            queens = setOf(position1, position2, position3, position4),
            conflictingPositions = emptySet(),
            solved = true
        )

        gameStateMachine.state.test {
            assertThat(awaitItem()).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clears conflicts when removing queen`() = runTest {
        val position1 = Position(0, 0)

        gameStateMachine.onQueenPlaced(position1)
        FakeConflictsResolver.conflicts = setOf(Position(1, 1))
        gameStateMachine.onQueenPlaced(position1)

        val expected = GameState(
            boardSize = 4,
            queens = emptySet(),
            conflictingPositions = emptySet(),
            solved = false
        )

        gameStateMachine.state.test {
            assertThat(awaitItem()).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `resets game state`() = runTest {
        val position = Position(0, 0)

        gameStateMachine.onQueenPlaced(position)
        gameStateMachine.reset()

        val expected = GameState(
            boardSize = 4,
            queens = emptySet(),
            conflictingPositions = emptySet(),
            solved = false
        )

        gameStateMachine.state.test {
            assertThat(awaitItem()).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
