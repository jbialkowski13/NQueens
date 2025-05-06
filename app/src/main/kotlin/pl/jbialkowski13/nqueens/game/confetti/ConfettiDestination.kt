package pl.jbialkowski13.nqueens.game.confetti

import kotlinx.serialization.Serializable
import pl.jbialkowski13.nqueens.navigation.Destination

@Serializable
internal data class ConfettiDestination(val durationMillis: Long) : Destination
