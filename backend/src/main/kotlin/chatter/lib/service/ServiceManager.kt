package chatter.lib.service

import arrow.fx.coroutines.continuations.ResourceScope
import chatter.lib.app.AppScope
import chatter.lib.log.getValue
import co.touchlab.kermit.Logger
import com.squareup.anvil.annotations.optional.SingleIn
import kotlinx.coroutines.awaitCancellation
import javax.inject.Inject

// manage all services
// for now this only means handling the lifecycle of the all `StatefulServices`
@SingleIn(AppScope::class)
class ServiceManager @Inject constructor(
    private val services: Set<@JvmSuppressWildcards StatefulService>
) {
    private val logger by Logger

    // bind all `StatefulServices` to the `ResourceScope` and
    // then start the services
    //
    // for now the services cant depend on other services
    // so we don't need to determine startup sequence and
    // just iterate over all services
    context(ResourceScope)
    suspend fun startServices() {
        logger.i { "Starting stateful services" }

        // first acquire and register the release of all services
        val boundServices = services.map {
            install(
                acquire = { it.also { s -> s.acquire() } },
                release = { s, _ -> s.release() }
            )
        }

        // then start the services (only all of them were properly initialised)
        boundServices.forEach { it.start() }

        // wait until we are cancelled
        // when `StatefulService.release()` will be automatically called
        awaitCancellation()
    }
}
