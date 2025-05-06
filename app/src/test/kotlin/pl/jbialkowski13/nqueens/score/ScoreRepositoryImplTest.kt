package pl.jbialkowski13.nqueens.score

import app.cash.turbine.test
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.hasClass
import assertk.assertions.isEqualTo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import pl.jbialkowski13.nqueens.db.Score as DbScore
import pl.jbialkowski13.nqueens.utils.FakeAppDatabase
import pl.jbialkowski13.nqueens.utils.FakeScoreDao
import pl.jbialkowski13.nqueens.utils.FakeTimeProvider
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.seconds

internal class ScoreRepositoryImplTest {

    private val fakeScoreDao = FakeScoreDao()
    private val appDatabase = FakeAppDatabase(fakeScoreDao = fakeScoreDao)
    private val timeProvider = FakeTimeProvider()

    private val scoreRepository = ScoreRepositoryImpl(
        database = appDatabase,
        timeProvider = timeProvider
    )

    @Test
    fun `saves score to database`() = runTest {
        scoreRepository.saveScore(boardSize = 8, duration = 30.seconds)
        val actual = fakeScoreDao.lastSavedScore

        val expected = DbScore(
            id = 0,
            boardSize = 8,
            time = 30.seconds,
            timestamp = timeProvider.instant
        )

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `returns success when score is saved`() = runTest {
        val result = scoreRepository.saveScore(boardSize = 8, duration = 30.seconds)

        assertThat(result).isEqualTo(SaveScoreResult.Success)
    }

    @Test
    fun `returns failure when database throws exception for saving`() = runTest {
        fakeScoreDao.saveScoreThrowable = Exception("Database error")
        val result = scoreRepository.saveScore(boardSize = 8, duration = 30.seconds)

        assertThat(result).isEqualTo(SaveScoreResult.Failure)
    }

    @Test
    fun `does not catch the CancellationException for saving`() = runTest {
        fakeScoreDao.saveScoreThrowable = CancellationException()

        assertFailure { scoreRepository.saveScore(boardSize = 8, duration = 30.seconds) }
            .hasClass(CancellationException::class)
    }

    @Test
    fun `emits scores`() = runTest {
        val score1 = DbScore(
            id = 0,
            boardSize = 8,
            time = 30.seconds,
            timestamp = timeProvider.instant
        )

        val score2 = DbScore(
            id = 1,
            boardSize = 10,
            time = 20.seconds,
            timestamp = timeProvider.instant
        )

        fakeScoreDao.scoresFlow = flowOf(listOf(score1, score2))

        val expected = listOf(
            Score(
                id = 0,
                boardSize = 8,
                time = 30.seconds,
                timestamp = timeProvider.instant
            ),
            Score(
                id = 1,
                boardSize = 10,
                time = 20.seconds,
                timestamp = timeProvider.instant
            )
        )

        scoreRepository.scores().test {
            val actual = awaitItem()
            assertThat(actual).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `does not emit same elements`() = runTest {
        val score = DbScore(
            id = 0,
            boardSize = 8,
            time = 30.seconds,
            timestamp = timeProvider.instant
        )

        val scoresFlow = MutableSharedFlow<List<DbScore>>()
        fakeScoreDao.scoresFlow = scoresFlow

        scoreRepository.scores().test {
            scoresFlow.emit(listOf(score))
            skipItems(1)
            scoresFlow.emit(listOf(score))
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits empty list when database throws exception`() = runTest {
        fakeScoreDao.scoresFlowThrowable = Exception("Database error")
        scoreRepository.scores().test {
            val actual = awaitItem()
            assertThat(actual).isEqualTo(emptyList())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `does not catch the CancellationException for scores`() = runTest {
        val error = CancellationException()
        fakeScoreDao.scoresFlowThrowable = error

        scoreRepository.scores().test {
            val actual = awaitError()
            assertThat(actual).isEqualTo(error)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
