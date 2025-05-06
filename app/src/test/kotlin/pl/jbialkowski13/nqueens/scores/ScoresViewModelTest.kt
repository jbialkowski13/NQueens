package pl.jbialkowski13.nqueens.scores

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import pl.jbialkowski13.nqueens.FakeLocaleProvider
import pl.jbialkowski13.nqueens.coroutines.CoroutineDispatchers
import pl.jbialkowski13.nqueens.duration.DurationFormatter
import pl.jbialkowski13.nqueens.navigation.ScoresDestination
import pl.jbialkowski13.nqueens.number.NumberFormatter
import pl.jbialkowski13.nqueens.score.Score
import pl.jbialkowski13.nqueens.time.TimeFormatter
import pl.jbialkowski13.nqueens.utils.CoroutinesExtension
import pl.jbialkowski13.nqueens.utils.FakeNavigator
import pl.jbialkowski13.nqueens.utils.FakeScoreRepository
import java.time.Instant
import kotlin.time.Duration.Companion.seconds

@ExtendWith(CoroutinesExtension::class)
internal class ScoresViewModelTest(dispatchers: CoroutineDispatchers) {

    private val fakeScoreRepository = FakeScoreRepository()
    private val localeProvider = FakeLocaleProvider()
    private val numberFormatter = NumberFormatter(
        localeProvider = localeProvider
    )
    private val durationFormatter = DurationFormatter(
        localeProvider = localeProvider
    )
    private val timeFormatter = TimeFormatter()
    private val navigator = FakeNavigator()

    private val viewModel = ScoresViewModel(
        scoreRepository = fakeScoreRepository,
        numberFormatter = numberFormatter,
        durationFormatter = durationFormatter,
        timeFormatter = timeFormatter,
        navigator = navigator,
        dispatchers = dispatchers
    )

    @Test
    fun `has correct initial state`() {
        val expected = ScoresUiState(
            scores = persistentListOf()
        )

        assertThat(viewModel.state).isEqualTo(expected)
    }

    @Test
    fun `navigates back on back click`() = runTest {
        navigator.destinations.add(ScoresDestination)

        viewModel.onBackClick()
        runCurrent()
        assertThat(navigator.destinations).isEmpty()
    }

    @Test
    fun `creates scores ui state out of scores from repository`() = runTest {
        val instant = Instant.parse("2023-10-01T12:00:00Z")

        val score = Score(
            id = 1L,
            boardSize = 8,
            time = 12.seconds,
            timestamp = instant
        )

        fakeScoreRepository.scores = listOf(score)

        viewModel.init()

        viewModel.collectData().test {
            skipItems(1) // skip initial emission

            val expected = ScoresUiState(
                scores = persistentListOf(
                    Score(
                        id = 1L,
                        time = "00:12",
                        boardSize = "8",
                        timestamp = "2023-10-01 14:00"
                    )
                )
            )

            assertThat(viewModel.state).isEqualTo(expected)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
