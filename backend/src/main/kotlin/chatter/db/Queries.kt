package chatter.db

import app.cash.sqldelight.Query
import arrow.core.raise.Raise
import chatter.lib.coroutines.Virtual
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// NOTE: Every query is run on the `Dispatchers.Virtual` dispatcher
// since the queries are blocking. Using the new virtual threads
// features this makes them non blocking

suspend fun <T : Any> Query<T>.asOptional() =
    withContext(Dispatchers.Virtual) { executeAsOneOrNull() }

// if the value was not found raise the provided error
context(Raise<E>)
suspend fun <T : Any, E> Query<T>.asOne(error: () -> E) = asOptional() ?: raise(error())

// only use when it is certain that the query will result in a value
suspend fun <T : Any> Query<T>.asOneInfallible() = withContext(Dispatchers.Virtual) { executeAsOne() }

suspend fun <T : Any> Query<T>.asList() =
    withContext(Dispatchers.Virtual) { executeAsList() }

// run an action under the `Dispatchers.Virtual` dispatcher
//
// useful for queries like insert, update or delete
// where they are just executed without returning a `Query<T>` first
suspend fun <R> withDb(
    block: suspend () -> R
): R = withContext(Dispatchers.Virtual) {
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

