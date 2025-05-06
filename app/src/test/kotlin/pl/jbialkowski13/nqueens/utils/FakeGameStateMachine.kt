package pl.jbialkowski13.nqueens.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pl.jbialkowski13.nqueens.game.GameState
import pl.jbialkowski13.nqueens.game.GameStateMachine
import pl.jbialkowski13.nqueens.game.Position

internal class FakeGameStateMachine : GameStateMachine {

    sealed interface QueenPlacementResult {
        data object Add : QueenPlacementResult
        data object Remove : QueenPlacementResult
        data class Conflict(val positions: Set<Position>) : QueenPlacementResult
        data object Solved : QueenPlacementResult
    }

    var queenPlacementResult: QueenPlacementResult = QueenPlacementResult.Add

    private val _state = MutableStateFlow<GameState>(
        GameState(
            boardSize = 4,
            queens = setOf(),
            conflictingPositions = setOf(),
            solved = false
        )
    )


    override val state: Flow<GameState>
        get() = _state

    override fun onQueenPlaced(position: Position) {
        var result = queenPlacementResult
        val newState = when (result) {
            QueenPlacementResult.Add -> _state.value.copy(
                queens = _state.value.queens + position,
                conflictingPositions = emptySet()
            )

            QueenPlacementResult.Remove -> _state.value.copy(
                queens = _state.value.queens - position,
                conflictingPositions = emptySet()
            )

            is QueenPlacementResult.Conflict -> _state.value.copy(
                conflictingPositions = result.positions
            )
            QueenPlacementResult.Solved -> _state.value.copy(solved = true)
        }
        _state.value = newState
    }

    override fun reset() {
        _state.value = GameState(
            boardSize = 4,
            queens = setOf(),
            conflictingPositions = setOf(),
            solved = false
        )
    }
}
