package chatter.db

import chatter.lib.app.AppScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides

@Module
@ContributesTo(AppScope::class)
object DatabaseModule {
    @Provides
    fun providerUserQueries(db: DatabaseService) = db.queries.userQueries

    @Provides
    fun providerUserRefreshTokenQueries(db: DatabaseService) = db.queries.userRefreshTokenQueries
}
