package pl.jbialkowski13.nqueens.navigation

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import pl.jbialkowski13.nqueens.coroutines.ThrottledChannel
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.TimeSource

internal interface Navigator {
    suspend fun navigateTo(destination: Destination, clearBackStack: Boolean = false)
    suspend fun navigateBack()
}

internal interface NavigationEventListener {
    val events: Flow<NavigationEvent>
}

internal sealed interface NavigationEvent {
    data class NavigateTo(
        val destination: Destination,
        val clearBackStack: Boolean
    ) : NavigationEvent

    data object NavigateBack : NavigationEvent
}

@Singleton
internal class NavigatorImpl @Inject constructor(
    private val timeSource: TimeSource,
) : Navigator, NavigationEventListener {

    private val _events = ThrottledChannel<NavigationEvent>(
        timeSource = timeSource,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
        areItemsTheSame = { first, second ->
            when {
                first is NavigationEvent.NavigateTo && second is NavigationEvent.NavigateTo ->
                    first.destination::class.qualifiedName == second.destination::class.qualifiedName

                else -> first == second
            }
        }
    )

    override val events: Flow<NavigationEvent> = _events.receiveAsFlow()

    override suspend fun navigateTo(destination: Destination, clearBackStack: Boolean) {
        val event = NavigationEvent.NavigateTo(
            destination = destination,
            clearBackStack = clearBackStack
        )
        _events.send(event)
    }

    override suspend fun navigateBack() {
        _events.send(NavigationEvent.NavigateBack)
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal interface NavigatorModule {

    @Binds
    fun bindNavigator(impl: NavigatorImpl): Navigator

    @Binds
    fun bindNavigationEventListener(impl: NavigatorImpl): NavigationEventListener
}
