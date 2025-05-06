package pl.jbialkowski13.nqueens.time

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Instant
import javax.inject.Inject
import kotlin.time.TimeMark
import kotlin.time.TimeSource

internal interface TimeProvider {
    fun markNow(): TimeMark
    fun instant(): Instant
}

internal class TimeProviderImpl @Inject constructor() : TimeProvider {
    override fun markNow(): TimeMark {
        return TimeSource.Monotonic.markNow()
    }

    override fun instant(): Instant {
        return Instant.now()
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal interface TimeProviderModule {

    @Binds
    fun bind(impl: TimeProviderImpl): TimeProvider
}
