package chatter.lib.http.config

import io.ktor.server.application.*

// configure the features of the of the `HttpServer`
//
// with this interface we can modularize the configuration
// and make connect it with our di system. Since ktor
// just uses plain functions this would not have been possible otherwise
interface HttpApplicationConfig {
    fun Application.configure()
}
