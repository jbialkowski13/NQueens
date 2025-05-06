package pl.jbialkowski13.nqueens.time

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlin.time.TimeSource

@Module
@InstallIn(SingletonComponent::class)
internal class TimeSourceModule {

    @Provides
    fun provideTimeSource(): TimeSource = TimeSource.Monotonic
}
