package pl.jbialkowski13.nqueens.coroutines

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

internal interface CoroutineDispatchers {
    val default: CoroutineDispatcher
}

internal class CoroutineDispatchersImpl @Inject constructor() : CoroutineDispatchers {
    override val default: CoroutineDispatcher = Dispatchers.Default
}

@Module
@InstallIn(SingletonComponent::class)
internal interface CoroutineDispatchersModule {

    @Binds
    fun bind(impl: CoroutineDispatchersImpl): CoroutineDispatchers
}
