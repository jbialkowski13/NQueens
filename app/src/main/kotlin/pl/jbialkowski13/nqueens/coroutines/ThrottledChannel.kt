package pl.jbialkowski13.nqueens.coroutines

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource

internal class ThrottledChannel<T>(
    private val duration: Duration = 300.milliseconds,
    private val timeSource: TimeSource = TimeSource.Monotonic,
    private val areItemsTheSame: (T, T) -> Boolean = { first, second -> first == second },
    onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND
) {
    private val channel = Channel<T>(onBufferOverflow = onBufferOverflow)

    private var lastElement: T? = null
    private var lastElementTime: TimeMark? = null

    suspend fun send(element: T) {
        val lastElementToCompare = lastElement
        val isTheSameAsLast =
            lastElementToCompare != null && areItemsTheSame(element, lastElementToCompare)
        val elapsed = lastElementTime?.elapsedNow()

        if (!isTheSameAsLast || elapsed == null || elapsed >= duration) {
            channel.send(element)
            lastElement = element
            lastElementTime = timeSource.markNow()
        }
    }

    fun receiveAsFlow(): Flow<T> = channel.receiveAsFlow()
}
