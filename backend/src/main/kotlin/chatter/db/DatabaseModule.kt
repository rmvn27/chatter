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

    @Provides
    fun provideTeamInviteQueries(db: DatabaseService) = db.queries.teamInviteQueries

    @Provides
    fun provideTeamParticipantQueries(db: DatabaseService) = db.queries.teamParticipantQueries

    @Provides
    fun provideTeamChannelQueries(db: DatabaseService) = db.queries.teamChannelQueries

    @Provides
    fun provideTeamMessageQueries(db: DatabaseService) = db.queries.teamMessageQueries
}
