package pl.jbialkowski13.nqueens.game

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import javax.inject.Inject
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeMark

internal interface Stopwatch {
    fun start(from: TimeMark): Flow<Duration>
}

internal class StopwatchImpl @Inject constructor() : Stopwatch {

    override fun start(from: TimeMark): Flow<Duration> {
        var lastEmittedSeconds: Long = -1

        return flow {
            while (coroutineContext.isActive) {
                val elapsed = from.elapsedNow()
                val currentSeconds = elapsed.inWholeSeconds

                if (currentSeconds != lastEmittedSeconds) {
                    lastEmittedSeconds = currentSeconds
                    emit(elapsed)
                }

                delay(100.milliseconds)
            }
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal interface StopwatchModule {

    @Binds
    fun bind(impl: StopwatchImpl): Stopwatch
}
