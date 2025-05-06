package pl.jbialkowski13.nqueens.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import kotlin.time.Duration

@Entity(
    tableName = "scores"
)
data class Score(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val boardSize: Int,
    val time: Duration,
    val timestamp: Instant
)
