package pl.jbialkowski13.nqueens.score

import java.time.Instant
import kotlin.time.Duration

internal data class Score(
    val id: Long,
    val boardSize: Int,
    val time: Duration,
    val timestamp: Instant
)
