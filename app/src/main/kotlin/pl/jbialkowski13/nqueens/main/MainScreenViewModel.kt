package pl.jbialkowski13.nqueens.main

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import pl.jbialkowski13.nqueens.coroutines.CoroutineDispatchers
import pl.jbialkowski13.nqueens.navigation.GameDestination
import pl.jbialkowski13.nqueens.navigation.Navigator
import pl.jbialkowski13.nqueens.navigation.ScoresDestination
import pl.jbialkowski13.nqueens.number.NumberFormatter
import javax.inject.Inject

@Stable
@HiltViewModel
internal class MainScreenViewModel @Inject constructor(
    private val numberFormatter: NumberFormatter,
    private val navigator: Navigator,
    private val dispatchers: CoroutineDispatchers
) : ViewModel() {

    private val internalStateFlow = MutableStateFlow(initialInternalState)

    private var stateJob: Job? = null

    var state by mutableStateOf(initialState)
        private set

    fun init() {
        if (stateJob == null) {
            stateJob = viewModelScope.launch {
                internalStateFlow
                    .map { internalState -> internalState.toUiState() }
                    .flowOn(dispatchers.default)
                    .collect { uiState -> state = uiState }
            }
        }
    }

    fun onStartGameClick() {
        viewModelScope.launch {
            navigator.navigateTo(GameDestination(boardSize = internalStateFlow.value.currentSize))
        }
    }

    fun onScoresClick() {
        viewModelScope.launch {
            navigator.navigateTo(ScoresDestination)
        }
    }

    fun onIncrementClick() {
        val currentSize = internalStateFlow.value.currentSize
        val newSize = currentSize + 1

        internalStateFlow.value = internalStateFlow.value.copy(
            currentSize = newSize,
            decrementEnabled = true,
            incrementEnabled = newSize < MAX_SIZE
        )
    }

    fun onDecrementClick() {
        val currentSize = internalStateFlow.value.currentSize
        val newSize = currentSize - 1

        internalStateFlow.value = internalStateFlow.value.copy(
            currentSize = newSize,
            decrementEnabled = newSize > MIN_SIZE,
            incrementEnabled = true
        )
    }

    private fun InternalState.toUiState(): MainUiState {
        return MainUiState(
            currentSize = numberFormatter.format(currentSize),
            decrementEnabled = decrementEnabled,
            incrementEnabled = incrementEnabled
        )
    }

    private data class InternalState(
        val currentSize: Int,
        val decrementEnabled: Boolean,
        val incrementEnabled: Boolean
    )

    private companion object {
        private const val MAX_SIZE = 20
        private const val MIN_SIZE = 4

        val initialInternalState = InternalState(
            currentSize = 4,
            decrementEnabled = false,
            incrementEnabled = true
        )

        val initialState = MainUiState(
            currentSize = "4",
            decrementEnabled = false,
            incrementEnabled = true
        )
    }
}
