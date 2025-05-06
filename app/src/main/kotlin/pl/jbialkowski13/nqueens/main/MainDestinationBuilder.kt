package pl.jbialkowski13.nqueens.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import pl.jbialkowski13.nqueens.navigation.DestinationBuilder
import pl.jbialkowski13.nqueens.navigation.MainDestination
import javax.inject.Inject

internal class MainDestinationBuilder @Inject constructor() : DestinationBuilder {

    override fun NavGraphBuilder.add(navController: NavController) {
        composable<MainDestination> { MainDestinationScreen() }
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal interface StartGameDestinationBuilderModule {

    @Binds
    @IntoSet
    fun bind(builder: MainDestinationBuilder): DestinationBuilder
}
