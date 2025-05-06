package pl.jbialkowski13.nqueens.navigation

import kotlinx.serialization.Serializable

@Serializable
data class GameDestination(val boardSize: Int) : Destination
