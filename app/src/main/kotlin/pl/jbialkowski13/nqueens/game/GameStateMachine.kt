package pl.jbialkowski13.nqueens.game

import dagger.Binds
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal data class GameState(
    val boardSize: Int,
    val queens: Set<Position>,
    val conflictingPositions: Set<Position>,
    val solved: Boolean
)

internal interface GameStateMachine {
    val state: Flow<GameState>
    fun onQueenPlaced(position: Position)
    fun reset()

    interface Factory {
        fun create(boardSize: Int): GameStateMachine
    }
}

internal class GameStateMachineImpl @AssistedInject constructor(
    @Assisted private val boardSize: Int,
    private val conflictsResolver: ConflictsResolver
) : GameStateMachine {

    @AssistedFactory
    internal interface GameStateMachineImplFactory : GameStateMachine.Factory {
        override fun create(boardSize: Int): GameStateMachineImpl
    }

    private val _flow = MutableStateFlow<GameState>(createInitialState())

    override val state: Flow<GameState>
        get() = _flow

    override fun onQueenPlaced(position: Position) {
        updateState(resolvePlacementState(position))
    }

    override fun reset() {
        updateState(createInitialState())
    }

    private fun createInitialState() = GameState(
        boardSize = boardSize,
        queens = emptySet(),
        conflictingPositions = emptySet(),
        solved = false
    )

    private fun resolvePlacementState(position: Position): GameState {
        val currentState = _flow.value
        val queens = currentState.queens

        return when {
            queens.contains(position) -> currentState.copy(
                queens = queens - position,
                conflictingPositions = emptySet()
            )

            else -> {
                val conflicts = conflictsResolver.resolve(queens, position)
                currentState.copy(
                    queens = if (conflicts.isEmpty()) queens + position else queens,
                    conflictingPositions = conflicts,
                    solved = conflicts.isEmpty() && queens.size + 1 == currentState.boardSize
                )
            }
        }
    }

    private fun updateState(newState: GameState) {
        _flow.value = newState
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal interface GameStateMachineModule {

    @Binds
    fun binds(impl: GameStateMachineImpl.GameStateMachineImplFactory): GameStateMachine.Factory
}
