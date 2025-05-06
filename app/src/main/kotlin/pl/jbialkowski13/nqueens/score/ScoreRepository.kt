package pl.jbialkowski13.nqueens.score

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pl.jbialkowski13.nqueens.db.AppDatabase
import pl.jbialkowski13.nqueens.db.DbResult
import pl.jbialkowski13.nqueens.db.queryForResult
import pl.jbialkowski13.nqueens.time.TimeProvider
import javax.inject.Inject
import kotlin.time.Duration
import pl.jbialkowski13.nqueens.db.Score as DbScore

internal interface ScoreRepository {
    suspend fun saveScore(boardSize: Int, duration: Duration): SaveScoreResult
    fun scores(): Flow<List<Score>>
}

internal class ScoreRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val timeProvider: TimeProvider
) : ScoreRepository {

    override suspend fun saveScore(boardSize: Int, duration: Duration): SaveScoreResult {
        val dbScore = DbScore(
            id = 0,
            boardSize = boardSize,
            time = duration,
            timestamp = timeProvider.instant()
        )

        return when (database.scoreDao().queryForResult { saveScore(dbScore) }) {
            is DbResult.Success -> SaveScoreResult.Success
            is DbResult.Failure -> SaveScoreResult.Failure
        }
    }

    override fun scores(): Flow<List<Score>> {
        return database
            .scoreDao()
            .getScores()
            .map { it.map { dbScore -> dbScore.toScore() } }
            .catch { exception ->
                if (exception is CancellationException) throw exception
                emit(emptyList())
            }
            .distinctUntilChanged()
    }

    private fun DbScore.toScore() = Score(
        id = id,
        boardSize = boardSize,
        time = time,
        timestamp = timestamp
    )
}

@Module
@InstallIn(SingletonComponent::class)
internal interface ScoreRepositoryModule {

    @Binds
    fun bind(impl: ScoreRepositoryImpl): ScoreRepository
}
