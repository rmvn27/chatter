package chatter.http

import arrow.core.raise.Raise
import chatter.errors.ApplicationError
import chatter.lib.http.getParam
import io.ktor.server.application.*

context(Raise<ApplicationError>)
val ApplicationCall.channelSlug
    get() = getParam("channelSlug")

context(Raise<ApplicationError>)
val ApplicationCall.teamSlug
    get() = getParam("teamSlug")
