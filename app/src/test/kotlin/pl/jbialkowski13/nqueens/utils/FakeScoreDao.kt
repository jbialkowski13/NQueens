package pl.jbialkowski13.nqueens.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import pl.jbialkowski13.nqueens.db.Score
import pl.jbialkowski13.nqueens.db.ScoreDao

internal class FakeScoreDao : ScoreDao() {

    var savedScores = mutableListOf<Score>()
    val lastSavedScore: Score?
        get() = savedScores.lastOrNull()

    var scoresFlow = emptyFlow<List<Score>>()

    var saveScoreThrowable: Throwable? = null
    var scoresFlowThrowable: Throwable? = null

    override suspend fun saveScore(score: Score) {
        saveScoreThrowable?.let { throw it }
        savedScores.add(score)
    }

    override fun getScores(): Flow<List<Score>> {
        return scoresFlowThrowable?.let { flow { throw it } } ?: scoresFlow
    }
}
