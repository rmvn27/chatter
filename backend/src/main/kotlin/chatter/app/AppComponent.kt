package chatter.app

import arrow.fx.coroutines.continuations.ResourceScope
import chatter.lib.app.AppScope
import chatter.lib.service.ServiceManager
import com.squareup.anvil.annotations.MergeComponent
import com.squareup.anvil.annotations.optional.SingleIn
import dagger.BindsInstance
import dagger.Component


// main entry point for the application
//
// the AppComponent is the entry for the di system
// where all of the properties will be provided. For starting
// the application we just need the `ServiceManager` which
// starts all stateful services which include the exposed API (HttpServer)
@SingleIn(AppScope::class)
@MergeComponent(AppScope::class)
interface AppComponent {
    companion object {
        fun create(config: ApplicationConfig) = DaggerAppComponent.factory().create(config)
    }

    val serviceManager: ServiceManager

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance config: ApplicationConfig): AppComponent
    }
}


// The `AppComponent` can't have functions that don't contribute to the injection
// so we have to define it as an extension function
context(ResourceScope)
suspend fun AppComponent.startServices() = serviceManager.startServices()
