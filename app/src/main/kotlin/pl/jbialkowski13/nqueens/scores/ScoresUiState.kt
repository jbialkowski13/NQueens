package pl.jbialkowski13.nqueens.scores

import kotlinx.collections.immutable.ImmutableList

internal data class ScoresUiState(
    val scores: ImmutableList<Score>
)

internal data class Score(
    val id: Long,
    val time: String,
    val boardSize: String,
    val timestamp: String
)
