package pl.jbialkowski13.nqueens.game.confetti

import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.isEqualTo
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import pl.jbialkowski13.nqueens.FakeLocaleProvider
import pl.jbialkowski13.nqueens.duration.DurationFormatter
import pl.jbialkowski13.nqueens.navigation.MainDestination
import pl.jbialkowski13.nqueens.utils.CoroutinesExtension
import pl.jbialkowski13.nqueens.utils.FakeNavigator

@ExtendWith(CoroutinesExtension::class)
internal class ConfettiViewModelTest {

    private val durationFormatter = DurationFormatter(
        localeProvider = FakeLocaleProvider()
    )

    private val navigator = FakeNavigator()

    private val viewModel = ConfettiViewModel(
        durationMillis = 1000L,
        durationFormatter = durationFormatter,
        navigator = navigator
    )

    @Test
    fun `has correct initial state`() {
        val expectedState = ConfettiUiState(
            time = "00:01"
        )
        assertThat(viewModel.state).isEqualTo(expectedState)
    }

    @Test
    fun `navigates to main destination with cleared back stack`() = runTest {
        navigator.destinations.add(ConfettiDestination(1000))
        navigator.destinations.add(ConfettiDestination(1000))

        viewModel.onCloseClick()
        runCurrent()
        assertThat(navigator.destinations).containsOnly(MainDestination)
    }
}
