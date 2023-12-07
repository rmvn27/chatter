package chatter.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.Transacter
import app.cash.sqldelight.TransactionWithoutReturn
import chatter.lib.AppDispatchers
import kotlinx.coroutines.withContext

// NOTE: Every query is run on a separate dispatcher for the db queries
// it runs as the IO dispatcher on a thread-pool of 64 threads
//
// there is also theoretically a reactive db connector that doesn't need
// a thread pool, but it doesn't work properly with sqldelight

suspend fun <T : Any> Query<T>.asOne() =
    withContext(AppDispatchers.db) { executeAsOne() }

suspend fun <T : Any> Query<T>.asOptional() =
    withContext(AppDispatchers.db) { executeAsOneOrNull() }

suspend fun <T : Any> Query<T>.asList() =
    withContext(AppDispatchers.db) { executeAsList() }

suspend fun <R> withDb(
    block: suspend () -> R
): R = withContext(AppDispatchers.db) {
    block()
}

suspend fun Transacter.withTransaction(
    noEnclosing: Boolean = false,
    body: TransactionWithoutReturn.() -> Unit,
) = withContext(AppDispatchers.db) {
    transaction(noEnclosing, body)
}
