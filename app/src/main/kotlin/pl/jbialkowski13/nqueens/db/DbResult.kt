package pl.jbialkowski13.nqueens.db

import kotlin.coroutines.cancellation.CancellationException

internal sealed interface DbResult<out T : Any?> {
    data class Success<T : Any?>(val value: T) : DbResult<T>
    data class Failure(val cause: Throwable) : DbResult<Nothing>
}

internal suspend fun <D : Dao, T> D.queryForResult(action: suspend D.() -> T): DbResult<T> {
    return try {
        DbResult.Success(action(this))
    } catch (expected: Throwable) {
        if (expected is CancellationException) throw expected
        DbResult.Failure(expected)
    }
}
