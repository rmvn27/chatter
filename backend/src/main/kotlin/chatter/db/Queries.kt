package chatter.db

import app.cash.sqldelight.Query
import arrow.core.raise.Raise
import chatter.lib.AppDispatchers
import kotlinx.coroutines.withContext

// NOTE: Every query is run on a separate dispatcher for the db queries
// it runs as the IO dispatcher on a thread-pool of 64 threads
//
// there is also theoretically a reactive db connector that doesn't need
// a thread pool, but it doesn't work properly with sqldelight

suspend fun <T : Any> Query<T>.asOptional() =
    withContext(AppDispatchers.db) { executeAsOneOrNull() }

// if the value was not found raise the provided error
context(Raise<E>)
suspend fun <T : Any, E> Query<T>.asOne(error: () -> E) = asOptional() ?: raise(error())

suspend fun <T : Any> Query<T>.asList() =
    withContext(AppDispatchers.db) { executeAsList() }

// run an action under the db dispatcher
//
// useful for queries like insert, update or delete
// where they are just executed without returning a `Query<T>` first
suspend fun <R> withDb(
    block: suspend () -> R
): R = withContext(AppDispatchers.db) {
    block()
}

// we often want to create an entity, insert it and then return it
//
// this extension method provides the convenience to insert the entity
// and then to return it
//
// since our entities are just simple data classes we can't sadly restrict
// the generic
suspend fun <T : Any> T.insert(insertQuery: (T) -> Unit): T = also {
    withDb { insertQuery(it) }
}

