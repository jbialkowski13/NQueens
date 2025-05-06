package pl.jbialkowski13.nqueens.locale

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.Locale
import javax.inject.Inject

internal interface LocaleProvider {
    val current: Locale
}

internal class LocaleProviderImpl @Inject constructor() : LocaleProvider {
    override val current: Locale
        get() = Locale.getDefault()
}

@Module
@InstallIn(SingletonComponent::class)
internal interface LocaleModule {

    @Binds
    fun bind(impl: LocaleProviderImpl): LocaleProvider
}
