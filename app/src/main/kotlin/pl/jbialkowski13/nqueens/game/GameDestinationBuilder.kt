package pl.jbialkowski13.nqueens.game

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.Serializable
import pl.jbialkowski13.nqueens.game.confetti.ConfettiDestination
import pl.jbialkowski13.nqueens.game.confetti.ConfettiScreen
import pl.jbialkowski13.nqueens.navigation.Destination
import pl.jbialkowski13.nqueens.navigation.DestinationBuilder
import pl.jbialkowski13.nqueens.navigation.GameDestination
import javax.inject.Inject

@Serializable
internal data object RootGameDestination : Destination

internal class GameDestinationBuilder @Inject constructor() : DestinationBuilder {

    override fun NavGraphBuilder.add(navController: NavController) {
        navigation<GameDestination>(
            startDestination = RootGameDestination
        ) {
            composable<RootGameDestination> { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry<GameDestination>()
                }
                val parentRoute = parentEntry.toRoute<GameDestination>()
                GameScreen(boardSize = parentRoute.boardSize)
            }

            composable<ConfettiDestination> {
                val route = it.toRoute<ConfettiDestination>()
                ConfettiScreen(durationMillis = route.durationMillis)
            }
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal interface GameDestinationBuilderModule {

    @Binds
    @IntoSet
    fun bind(builder: GameDestinationBuilder): DestinationBuilder
}
