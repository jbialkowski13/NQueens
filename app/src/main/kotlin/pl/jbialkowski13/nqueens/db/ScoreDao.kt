package pl.jbialkowski13.nqueens.db

import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import androidx.room.Dao as RoomDao

@RoomDao
internal abstract class ScoreDao : Dao {

    @Insert(onConflict = REPLACE)
    abstract suspend fun saveScore(score: Score)

    @Query("SELECT * FROM scores ORDER BY boardSize DESC, time ASC")
    abstract fun getScores(): Flow<List<Score>>
}
