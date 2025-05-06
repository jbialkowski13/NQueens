package pl.jbialkowski13.nqueens.game

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TestTimeSource

internal class StopwatchImplTest {

    private val testTimeSource = TestTimeSource()
    private val stopwatch = StopwatchImpl()

    @Test
    fun `emits elapsed duration`() = runTest {
        val from = testTimeSource.markNow()

        stopwatch.start(from).test {
            assertThat(awaitItem()).isEqualTo(0.seconds)

            testTimeSource += 1.seconds
            assertThat(awaitItem()).isEqualTo(1.seconds)

            testTimeSource += 1.seconds
            assertThat(awaitItem()).isEqualTo(2.seconds)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits elapsed duration only when the seconds change`() = runTest {
        val from = testTimeSource.markNow()

        stopwatch.start(from).test {
            assertThat(awaitItem()).isEqualTo(0.seconds)

            testTimeSource += 1.seconds
            assertThat(awaitItem()).isEqualTo(1.seconds)

            testTimeSource += 500.milliseconds
            expectNoEvents()

            testTimeSource += 499.milliseconds
            expectNoEvents()

            testTimeSource += 1.milliseconds
            assertThat(awaitItem()).isEqualTo(2.seconds)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
