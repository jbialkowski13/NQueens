package pl.jbialkowski13.nqueens.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import pl.jbialkowski13.nqueens.game.Stopwatch
import kotlin.time.Duration
import kotlin.time.TimeMark

internal class FakeStopwatch : Stopwatch {

    private var durationFlow = MutableStateFlow<Duration>(Duration.ZERO)
    var from: TimeMark? = null

    override fun start(from: TimeMark): Flow<Duration> {
        this.from = from
        return durationFlow
    }

    suspend fun emit(duration: Duration) {
        durationFlow.emit(duration)
    }
}
