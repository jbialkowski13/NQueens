package pl.jbialkowski13.nqueens.game

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import pl.jbialkowski13.nqueens.FakeLocaleProvider
import pl.jbialkowski13.nqueens.coroutines.CoroutineDispatchers
import pl.jbialkowski13.nqueens.duration.DurationFormatter
import pl.jbialkowski13.nqueens.game.confetti.ConfettiDestination
import pl.jbialkowski13.nqueens.navigation.GameDestination
import pl.jbialkowski13.nqueens.number.NumberFormatter
import pl.jbialkowski13.nqueens.utils.CoroutinesExtension
import pl.jbialkowski13.nqueens.utils.FakeGameStateMachine
import pl.jbialkowski13.nqueens.utils.FakeNavigator
import pl.jbialkowski13.nqueens.utils.FakeScoreRepository
import pl.jbialkowski13.nqueens.utils.FakeStopwatch
import pl.jbialkowski13.nqueens.utils.FakeTimeProvider
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TestTimeSource

@ExtendWith(CoroutinesExtension::class)
internal class GameScreenViewModelTest(dispatchers: CoroutineDispatchers) {

    private val gameStateMachine = FakeGameStateMachine()
    private val gameStateMachineFactory = object : GameStateMachine.Factory {
        override fun create(boardSize: Int): GameStateMachine = gameStateMachine
    }
    private val localeProvider = FakeLocaleProvider()
    private val numberFormatter = NumberFormatter(
        localeProvider = localeProvider
    )
    private val durationFormatter = DurationFormatter(
        localeProvider = localeProvider
    )
    private val stopwatch = FakeStopwatch()
    private val timeProvider = FakeTimeProvider()
    private val scoreRepository = FakeScoreRepository()
    private val navigator = FakeNavigator()

    private val viewModel = GameScreenViewModel(
        boardSize = 4,
        gameStateMachineFactory = gameStateMachineFactory,
        numberFormatter = numberFormatter,
        durationFormatter = durationFormatter,
        stopwatch = stopwatch,
        timeProvider = timeProvider,
        scoreRepository = scoreRepository,
        navigator = navigator,
        dispatchers = dispatchers
    )

    @Test
    fun `has correct initial state`() {
        val expected = GameUiState(
            boardState = BoardState(
                size = 4,
                queens = persistentSetOf(),
                conflictingPositions = persistentSetOf()
            ),
            queensLeft = "4",
            time = "00:00",
            displayCloseConfirmation = false
        )

        assertThat(viewModel.state).isEqualTo(expected)
    }

    @Test
    fun `sets displayCloseConfirmation to true when close click`() = runTest {
        viewModel.init()
        viewModel.onCloseClick()
        runCurrent()

        assertThat(viewModel.state.displayCloseConfirmation).isEqualTo(true)
    }

    @Test
    fun `sets displayCloseConfirmation to false when close confirmed`() = runTest {
        viewModel.init()
        viewModel.onCloseClick()
        runCurrent()

        viewModel.onCloseConfirmed()
        runCurrent()

        assertThat(viewModel.state.displayCloseConfirmation).isEqualTo(false)
    }

    @Test
    fun `navigates back when close confirmed`() = runTest {
        navigator.destinations.add(GameDestination(4))

        viewModel.init()
        viewModel.onCloseConfirmed()
        runCurrent()

        assertThat(navigator.destinations).isEmpty()
    }

    @Test
    fun `sets displayCloseConfirmation to false when close cancelled`() = runTest {
        viewModel.init()
        viewModel.onCloseClick()
        runCurrent()

        viewModel.onCloseCancelled()
        runCurrent()

        assertThat(viewModel.state.displayCloseConfirmation).isEqualTo(false)
    }

    @Test
    fun `starts stopwatch when collected`() = runTest {
        viewModel.init()
        runCurrent()

        viewModel.collectData().test {
            stopwatch.emit(15.seconds)
            runCurrent()
            assertThat(viewModel.state.time).isEqualTo("00:15")
        }
    }

    @Test
    fun `stops stopwatch when cancelled`() = runTest {
        viewModel.init()
        runCurrent()

        viewModel.collectData().test {
            stopwatch.emit(15.seconds)
            runCurrent()
            cancelAndIgnoreRemainingEvents()
            runCurrent()

            stopwatch.emit(30.seconds)
            assertThat(viewModel.state.time).isEqualTo("00:15")
        }
    }

    @Test
    fun `adds duration when not collecting to next stopwatch as from time`() = runTest {
        val timeSource = TestTimeSource()
        val initialTimeMark = timeSource.markNow()
        timeProvider.timeSource = timeSource

        viewModel.init()
        runCurrent()

        timeSource += 15.seconds
        viewModel.collectData().test { cancelAndIgnoreRemainingEvents() }
        timeSource += 15.seconds

        viewModel.collectData().test { cancelAndIgnoreRemainingEvents() }
        assertThat(stopwatch.from).isEqualTo(initialTimeMark + 30.seconds)
    }

    @Test
    fun `places queen on board when position clicked`() = runTest {
        viewModel.init()
        runCurrent()

        gameStateMachine.queenPlacementResult = FakeGameStateMachine.QueenPlacementResult.Add

        viewModel.collectData().test {
            viewModel.onPositionClick(Position(0, 0))
            runCurrent()

            assertThat(viewModel.state.boardState.queens).containsOnly(Position(0, 0))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `removes queen from board when position clicked`() = runTest {
        viewModel.init()
        runCurrent()

        viewModel.collectData().test {
            gameStateMachine.queenPlacementResult = FakeGameStateMachine.QueenPlacementResult.Add
            viewModel.onPositionClick(Position(0, 0))
            runCurrent()

            gameStateMachine.queenPlacementResult = FakeGameStateMachine.QueenPlacementResult.Remove
            viewModel.onPositionClick(Position(0, 0))
            runCurrent()

            assertThat(viewModel.state.boardState.queens).isEmpty()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `shows conflict when position clicked`() = runTest {
        viewModel.init()
        runCurrent()

        gameStateMachine.queenPlacementResult = FakeGameStateMachine.QueenPlacementResult.Conflict(
            positions = setOf(Position(1, 0))
        )

        viewModel.collectData().test {
            viewModel.onPositionClick(Position(0, 0))
            runCurrent()

            assertThat(viewModel.state.boardState.conflictingPositions).containsOnly(Position(1, 0))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `resets game on restart click`() = runTest {
        viewModel.init()
        runCurrent()

        gameStateMachine.queenPlacementResult = FakeGameStateMachine.QueenPlacementResult.Add

        viewModel.collectData().test {
            viewModel.onPositionClick(Position(0, 0))
            runCurrent()

            viewModel.onRestartClick()
            runCurrent()

            assertThat(viewModel.state.boardState.queens).isEmpty()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `navigates to confetti when solved with duration from stopwatch`() = runTest {
        viewModel.init()
        runCurrent()

        gameStateMachine.queenPlacementResult = FakeGameStateMachine.QueenPlacementResult.Solved

        viewModel.collectData().test {
            stopwatch.emit(15.seconds)
            runCurrent()

            viewModel.onPositionClick(Position(0, 0))
            runCurrent()

            assertThat(navigator.lastDestination).isEqualTo(ConfettiDestination(15_000))

            cancelAndIgnoreRemainingEvents()
        }
    }
}
