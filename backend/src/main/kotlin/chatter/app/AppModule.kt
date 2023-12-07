package chatter.app

import chatter.lib.app.AppScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides

// for easier access for all of the configurations inside the `ApplicationConfig`
// provide them each individually
@Module
@ContributesTo(AppScope::class)
object AppModule {
    @Provides
    fun provideDbConfig(config: ApplicationConfig) = config.db

    @Provides
    fun provideAuthConfig(config: ApplicationConfig) = config.auth

    @Provides
    fun provideHttpConfig(config: ApplicationConfig) = config.http
}
