package pl.jbialkowski13.nqueens.coroutines

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TestTimeSource

internal class ThrottledChannelTest {

    private val timeSource = TestTimeSource()

    private val channel = ThrottledChannel<Int>(
        duration = 300.milliseconds,
        timeSource = timeSource,
        areItemsTheSame = { first, second -> first == second }
    )

    @Test
    fun `sends first element`() = runTest {
        channel.receiveAsFlow().test {
            channel.send(0)
            assertThat(awaitItem()).isEqualTo(0)
        }
    }

    @Test
    fun `sends two elements if they are sent within same timeframe and they are not equal`() = runTest {
        channel.receiveAsFlow().test {
            channel.send(0)
            assertThat(awaitItem()).isEqualTo(0)
            timeSource += 200.milliseconds
            channel.send(1)
            assertThat(awaitItem()).isEqualTo(1)
        }
    }

    @Test
    fun `sends only first element if they are sent within same timeframe and they are equal`() = runTest {
        channel.receiveAsFlow().test {
            channel.send(0)
            assertThat(awaitItem()).isEqualTo(0)
            timeSource += 200.milliseconds
            channel.send(0)
            assertThat(cancelAndConsumeRemainingEvents()).isEmpty()
        }
    }

    @Test
    fun `sends two elements if they are sent within two timeframes`() = runTest {
        channel.receiveAsFlow().test {
            channel.send(0)
            assertThat(awaitItem()).isEqualTo(0)
            timeSource += 301.milliseconds
            channel.send(0)
            assertThat(awaitItem()).isEqualTo(0)
        }
    }
}
