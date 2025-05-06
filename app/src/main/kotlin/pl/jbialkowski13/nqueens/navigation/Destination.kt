package pl.jbialkowski13.nqueens.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

interface Destination

interface DestinationBuilder {
    fun NavGraphBuilder.add(navController: NavController) {}
}
