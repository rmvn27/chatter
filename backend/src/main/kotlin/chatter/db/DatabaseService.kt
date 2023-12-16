package chatter.db

import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import chatter.lib.app.AppScope
import chatter.lib.coroutines.Virtual
import chatter.lib.log.getValue
import chatter.lib.service.StatefulService
import co.touchlab.kermit.Logger
import com.squareup.anvil.annotations.ContributesMultibinding
import com.squareup.anvil.annotations.optional.SingleIn
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import javax.inject.Inject

@SingleIn(AppScope::class)
@ContributesMultibinding(AppScope::class)
class DatabaseService @Inject constructor(
    private val config: Config
) : StatefulService {
    private val logger by Logger

    private val dataSourceConfig = HikariConfig().apply {
        dataSourceClassName = "org.postgresql.ds.PGSimpleDataSource"
        password = config.password
        username = config.user
        jdbcUrl = "jdbc:${config.user}://${config.host}:${config.port}"

        addDataSourceProperty("databaseName", config.database)

        validate()
    }

    private val dataSource = HikariDataSource(dataSourceConfig)
    private val driver by lazy { dataSource.asJdbcDriver() }

    val queries by lazy { DatabaseQueries(driver) }

    override suspend fun acquire() {
        logger.i { "Connecting" }

        // since we deploy only one instance of the application
        // we can do the db migrations internally
        val migrationService = MigrationService(DatabaseQueries.Schema, driver)
        migrationService.migrate()
    }

    override suspend fun release() = withContext(Dispatchers.Virtual) {
        logger.i { "Shutting down" }
        driver.close()
    }

    @Serializable
    data class Config(
        val host: String = "localhost",
        val port: Int = 5431,
        val database: String = "chatter",
        val user: String = "postgres",
        val password: String
    )
}
