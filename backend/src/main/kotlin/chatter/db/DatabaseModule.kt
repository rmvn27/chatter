package chatter.db

import chatter.lib.app.AppScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides

@Module
@ContributesTo(AppScope::class)
object DatabaseModule {
    @Provides
    fun provideUserQueries(db: DatabaseService) = db.queries.userQueries

    @Provides
    fun provideUserRefreshTokenQueries(db: DatabaseService) = db.queries.userRefreshTokenQueries

    @Provides
    fun provideTeamQueries(db: DatabaseService) = db.queries.teamQueries
}
