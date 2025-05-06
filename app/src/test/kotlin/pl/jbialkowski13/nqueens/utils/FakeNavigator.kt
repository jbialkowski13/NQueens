package pl.jbialkowski13.nqueens.utils

import pl.jbialkowski13.nqueens.navigation.Destination
import pl.jbialkowski13.nqueens.navigation.Navigator

internal class FakeNavigator : Navigator {

    var destinations = mutableListOf<Destination>()
    val lastDestination: Destination?
        get() = destinations.lastOrNull()

    override suspend fun navigateTo(destination: Destination, clearBackStack: Boolean) {
        if (clearBackStack) {
            destinations.clear()
        }
        destinations.add(destination)
    }

    override suspend fun navigateBack() {
        if (destinations.isNotEmpty()) {
            destinations.removeLast()
        }
    }
}
