package chatter.lib.service

// a service which holds a internal state that has to be managed with a lifecycle
//
// it can have the following methods for managing itself:
// - `acquire`: acquire all the necessary resources (eg. connections, file handles, ...)
// - `start`: if something will be run in the background eg.a http server start it here
// - `release`: if anything was started in the background stop it here
//              and then release all the acquired resources
interface StatefulService {
    suspend fun acquire() {}

    suspend fun start() {}

    suspend fun release() {}
}
