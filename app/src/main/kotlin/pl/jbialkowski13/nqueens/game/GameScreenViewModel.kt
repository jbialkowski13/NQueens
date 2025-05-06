package pl.jbialkowski13.nqueens.game

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.jbialkowski13.nqueens.coroutines.CoroutineDispatchers
import pl.jbialkowski13.nqueens.duration.DurationFormatter
import pl.jbialkowski13.nqueens.game.confetti.ConfettiDestination
import pl.jbialkowski13.nqueens.navigation.Navigator
import pl.jbialkowski13.nqueens.number.NumberFormatter
import pl.jbialkowski13.nqueens.score.SaveScoreResult
import pl.jbialkowski13.nqueens.score.ScoreRepository
import pl.jbialkowski13.nqueens.time.TimeProvider
import kotlin.time.Duration
import kotlin.time.TimeMark

@Stable
@HiltViewModel(assistedFactory = GameScreenViewModel.Factory::class)
internal class GameScreenViewModel @AssistedInject constructor(
    @Assisted private val boardSize: Int,
    private val gameStateMachineFactory: GameStateMachine.Factory,
    private val numberFormatter: NumberFormatter,
    private val durationFormatter: DurationFormatter,
    private val stopwatch: Stopwatch,
    private val timeProvider: TimeProvider,
    private val scoreRepository: ScoreRepository,
    private val navigator: Navigator,
    private val dispatchers: CoroutineDispatchers
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(boardSize: Int): GameScreenViewModel
    }

    sealed interface UiEffect {
        data object ShowSaveScoreError : UiEffect
    }

    private val gameStateMachine by lazy { gameStateMachineFactory.create(boardSize) }

    private var internalStateFlow = MutableStateFlow(createInternalState())
    private var uiEffectsFlow = MutableSharedFlow<UiEffect>()
    private var stateJob: Job? = null

    private var pausedDuration = Duration.ZERO
    private var lastPausedTimeMark: TimeMark? = null
    private var startTimeMark: TimeMark? = null
    private var stopwatchJob: Job? = null

    private val gameFlow = gameStateMachine.state
        .onEach { gameState -> internalStateFlow.update { it.copy(gameState = gameState) } }
        .flowOn(dispatchers.default)

    private val solvedStateFlow = gameStateMachine.state
        .filter { it.solved }
        .onEach {
            stopStopwatch()
            saveScoreAndShowConfetti()
        }
        .flowOn(dispatchers.default)

    var state by mutableStateOf(createInitialState())
        private set

    var uiEffects: Flow<UiEffect> = uiEffectsFlow

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

    fun collectData(): Flow<*> {
        return combine(gameFlow, solvedStateFlow) { }
            .onCompletion {
                lastPausedTimeMark = timeProvider.markNow()
                stopStopwatch()
            }
            .onStart {
                measurePausedDuration()
                startStopwatch()
            }
    }

    fun onCloseClick() {
        internalStateFlow.value = internalStateFlow.value.copy(closeRequested = true)
    }

    fun onCloseConfirmed() {
        internalStateFlow.value = internalStateFlow.value.copy(closeRequested = false)
        viewModelScope.launch { navigator.navigateBack() }
    }

    fun onCloseCancelled() {
        internalStateFlow.value = internalStateFlow.value.copy(closeRequested = false)
    }

    fun onRestartClick() {
        gameStateMachine.reset()
        pausedDuration = Duration.ZERO
        lastPausedTimeMark = null
        startTimeMark = null
        stopStopwatch()
        startStopwatch()
    }

    fun onPositionClick(position: Position) {
        gameStateMachine.onQueenPlaced(position)
    }

    private fun measurePausedDuration() {
        lastPausedTimeMark?.let {
            pausedDuration += it.elapsedNow()
            lastPausedTimeMark = null
        }
    }

    private fun startStopwatch() {
        val timeMark = startTimeMark ?: timeProvider.markNow().also { startTimeMark = it }

        if (stopwatchJob == null) {
            stopwatchJob = viewModelScope.launch {
                stopwatch
                    .start(timeMark + pausedDuration)
                    .flowOn(dispatchers.default)
                    .collect { duration ->
                        internalStateFlow.value = internalStateFlow.value.copy(duration = duration)
                    }
            }
        }
    }

    private fun stopStopwatch() {
        stopwatchJob?.cancel()
        stopwatchJob = null
    }

    private fun saveScoreAndShowConfetti() {
        val duration = internalStateFlow.value.duration

        viewModelScope.launch {
            val scoreResult = scoreRepository.saveScore(boardSize = boardSize, duration = duration)
            when (scoreResult) {
                SaveScoreResult.Success -> {
                    val destination =
                        ConfettiDestination(durationMillis = duration.inWholeMilliseconds)
                    navigator.navigateTo(destination)
                }

                SaveScoreResult.Failure -> {
                    viewModelScope.launch {
                        uiEffectsFlow.emit(UiEffect.ShowSaveScoreError)
                    }
                }
            }
        }
    }

    private fun InternalState.toUiState(): GameUiState {
        return GameUiState(
            boardState = BoardState(
                size = gameState.boardSize,
                queens = gameState.queens.toPersistentSet(),
                conflictingPositions = gameState.conflictingPositions.toPersistentSet()
            ),
            queensLeft = numberFormatter.format(boardSize - gameState.queens.size),
            time = durationFormatter.format(duration),
            displayCloseConfirmation = closeRequested
        )
    }

    private data class InternalState(
        val gameState: GameState,
        val duration: Duration,
        val closeRequested: Boolean
    )

    private fun createInternalState() = InternalState(
        gameState = GameState(
            queens = emptySet(),
            conflictingPositions = emptySet(),
            boardSize = boardSize,
            solved = false
        ),
        duration = Duration.ZERO,
        closeRequested = false
    )

    private fun createInitialState() = GameUiState(
        boardState = BoardState(
            size = boardSize,
            queens = persistentSetOf(),
            conflictingPositions = persistentSetOf()
        ),
        queensLeft = numberFormatter.format(boardSize),
        time = durationFormatter.format(Duration.ZERO),
        displayCloseConfirmation = false
    )
}
