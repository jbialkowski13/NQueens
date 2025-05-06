package pl.jbialkowski13.nqueens.scores

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import pl.jbialkowski13.nqueens.navigation.DestinationBuilder
import pl.jbialkowski13.nqueens.navigation.ScoresDestination
import javax.inject.Inject

internal class ScoresDestinationBuilder @Inject constructor() : DestinationBuilder {

    override fun NavGraphBuilder.add(navController: NavController) {
        composable<ScoresDestination> {
            ScoresScreen()
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal interface ScoresDestinationModule {

    @Binds
    @IntoSet
    fun bind(impl: ScoresDestinationBuilder): DestinationBuilder
}
