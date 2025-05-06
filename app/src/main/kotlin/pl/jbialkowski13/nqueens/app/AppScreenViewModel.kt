package pl.jbialkowski13.nqueens.app

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import pl.jbialkowski13.nqueens.navigation.DestinationBuilder
import pl.jbialkowski13.nqueens.navigation.NavigationEventListener

@Stable
@HiltViewModel
internal class AppScreenViewModel @Inject constructor(
    val destinationBuilders: Set<@JvmSuppressWildcards DestinationBuilder>,
    val navigationEventListener: NavigationEventListener
) : ViewModel()
