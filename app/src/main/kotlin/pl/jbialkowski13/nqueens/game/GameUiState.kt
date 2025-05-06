package pl.jbialkowski13.nqueens.game

import kotlinx.collections.immutable.ImmutableSet

internal data class GameUiState(
    val boardState: BoardState,
    val queensLeft: String,
    val time: String,
    val displayCloseConfirmation: Boolean
)

internal data class BoardState(
    val size: Int,
    val queens: ImmutableSet<Position>,
    val conflictingPositions: ImmutableSet<Position>
)
