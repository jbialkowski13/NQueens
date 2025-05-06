package pl.jbialkowski13.nqueens.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import pl.jbialkowski13.nqueens.score.SaveScoreResult
import pl.jbialkowski13.nqueens.score.Score
import pl.jbialkowski13.nqueens.score.ScoreRepository
import kotlin.time.Duration

internal class FakeScoreRepository : ScoreRepository {

    var scores = emptyList<Score>()
    var saveScoreResult: SaveScoreResult = SaveScoreResult.Success

    data class SavedScore(val boardSize: Int, val duration: Duration)

    val savedScores = mutableListOf<SavedScore>()

    override suspend fun saveScore(
        boardSize: Int,
        duration: Duration
    ): SaveScoreResult {
        savedScores.add(SavedScore(boardSize, duration))
        return saveScoreResult
    }

    override fun scores(): Flow<List<Score>> = flowOf(scores)
}
