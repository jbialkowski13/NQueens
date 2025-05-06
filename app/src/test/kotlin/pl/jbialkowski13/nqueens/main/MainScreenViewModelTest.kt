package pl.jbialkowski13.nqueens.main

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import pl.jbialkowski13.nqueens.FakeLocaleProvider
import pl.jbialkowski13.nqueens.coroutines.CoroutineDispatchers
import pl.jbialkowski13.nqueens.navigation.GameDestination
import pl.jbialkowski13.nqueens.navigation.ScoresDestination
import pl.jbialkowski13.nqueens.number.NumberFormatter
import pl.jbialkowski13.nqueens.utils.CoroutinesExtension
import pl.jbialkowski13.nqueens.utils.FakeNavigator

@ExtendWith(CoroutinesExtension::class)
internal class MainScreenViewModelTest(dispatchers: CoroutineDispatchers) {

    private val numberFormatter = NumberFormatter(
        localeProvider = FakeLocaleProvider()
    )

    private val navigator = FakeNavigator()

    private val viewModel = MainScreenViewModel(
        numberFormatter = numberFormatter,
        navigator = navigator,
        dispatchers = dispatchers
    )

    @Test
    fun `has correct initial state`() {
        val expected = MainUiState(
            currentSize = "4",
            decrementEnabled = false,
            incrementEnabled = true
        )

        assertThat(viewModel.state).isEqualTo(expected)
    }

    @Test
    fun `navigates to game destination on start game click`() = runTest {
        viewModel.onStartGameClick()
        runCurrent()
        assertThat(navigator.lastDestination).isEqualTo(GameDestination(boardSize = 4))
    }

    @Test
    fun `navigates to scores destination on scores click`() = runTest {
        viewModel.onScoresClick()
        runCurrent()
        assertThat(navigator.lastDestination).isEqualTo(ScoresDestination)
    }

    @Test
    fun `increments size`() = runTest {
        viewModel.init()

        viewModel.onIncrementClick()
        runCurrent()

        assertThat(viewModel.state.currentSize).isEqualTo("5")
    }

    @Test
    fun `decrements size`() = runTest {
        viewModel.init()

        viewModel.onDecrementClick()
        runCurrent()

        assertThat(viewModel.state.currentSize).isEqualTo("3")
    }

    @Test
    fun `sets decrement enabled to false when size is 4`() = runTest {
        viewModel.init()

        viewModel.onIncrementClick()
        viewModel.onDecrementClick()
        runCurrent()

        assertThat(viewModel.state.decrementEnabled).isEqualTo(false)
    }

    @Test
    fun `sets increment enabled to false when size is 20`() = runTest {
        viewModel.init()

        repeat(16) { viewModel.onIncrementClick() }
        runCurrent()

        assertThat(viewModel.state.incrementEnabled).isEqualTo(false)
    }
}
