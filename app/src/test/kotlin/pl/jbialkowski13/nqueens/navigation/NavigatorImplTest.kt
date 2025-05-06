package pl.jbialkowski13.nqueens.navigation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TestTimeSource

internal class NavigatorImplTest {

    private val destination = object : Destination {}

    private val timeSource = TestTimeSource()

    private val navigator = NavigatorImpl(
        timeSource = timeSource
    )

    @Test
    fun `emits navigate event`() = runTest {
        navigator.events.test {
            navigator.navigateTo(destination, clearBackStack = false)
            val actual = awaitItem()
            val expected = NavigationEvent.NavigateTo(
                destination = destination,
                clearBackStack = false
            )

            assertThat(actual).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits navigate back event`() = runTest {
        navigator.events.test {
            navigator.navigateBack()
            val actual = awaitItem()
            val expected = NavigationEvent.NavigateBack

            assertThat(actual).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `does not emit duplicate navigate event within 300 milliseconds of time span`() = runTest {
        navigator.events.test {
            navigator.navigateTo(destination, clearBackStack = false)
            navigator.navigateTo(destination, clearBackStack = false)

            skipItems(1)
            expectNoEvents()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits duplicate navigate event after 300 milliseconds of time span`() = runTest {
        navigator.events.test {
            navigator.navigateTo(destination, clearBackStack = false)
            skipItems(1)

            timeSource += 300.milliseconds
            navigator.navigateTo(destination, clearBackStack = false)
            skipItems(1)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
