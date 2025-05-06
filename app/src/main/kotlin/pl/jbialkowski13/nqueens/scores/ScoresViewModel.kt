package pl.jbialkowski13.nqueens.scores

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.jbialkowski13.nqueens.coroutines.CoroutineDispatchers
import pl.jbialkowski13.nqueens.duration.DurationFormatter
import pl.jbialkowski13.nqueens.navigation.Navigator
import pl.jbialkowski13.nqueens.number.NumberFormatter
import pl.jbialkowski13.nqueens.score.ScoreRepository
import pl.jbialkowski13.nqueens.time.TimeFormatter
import javax.inject.Inject
import pl.jbialkowski13.nqueens.score.Score as DomainScore

@Stable
@HiltViewModel
internal class ScoresViewModel @Inject constructor(
    private val scoreRepository: ScoreRepository,
    private val numberFormatter: NumberFormatter,
    private val durationFormatter: DurationFormatter,
    private val timeFormatter: TimeFormatter,
    private val navigator: Navigator,
    private val dispatchers: CoroutineDispatchers
) : ViewModel() {

    private val internalStateFlow = MutableStateFlow<InternalState>(initialInternalState)
    private var stateJob: Job? = null

    var state by mutableStateOf(initialState)
        private set

    fun init() {
        if (stateJob == null) {
            stateJob = viewModelScope.launch {
                internalStateFlow
                    .map { it.toUiState() }
                    .flowOn(dispatchers.default)
                    .collect { newState -> state = newState }
            }
        }
    }

    fun collectData(): Flow<*> {
        return scoreRepository
            .scores()
            .onEach { scores -> internalStateFlow.update { it.copy(scores = scores) } }
            .flowOn(dispatchers.default)
    }

    fun onBackClick() {
        viewModelScope.launch { navigator.navigateBack() }
    }

    private fun InternalState.toUiState(): ScoresUiState = ScoresUiState(
        scores = scores.map { it.toUiScore() }.toPersistentList()
    )

    private fun DomainScore.toUiScore(): Score {
        return Score(
            id = id,
            boardSize = numberFormatter.format(boardSize),
            time = durationFormatter.format(time),
            timestamp = timeFormatter.formatAsLocalDateTime(timestamp)
        )
    }

    private data class InternalState(
        val scores: List<DomainScore>
    )

    private companion object {
        val initialInternalState = InternalState(
            scores = emptyList()
        )

        val initialState = ScoresUiState(
            scores = persistentListOf()
        )
    }
}
