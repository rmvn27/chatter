package chatter

import chatter.app.AppComponent
import chatter.app.ApplicationConfig
import chatter.app.startServices
import chatter.lib.app.runApp

fun main() = runApp {
    val config = ApplicationConfig.read()
    val appComponent = AppComponent.create(config)

    appComponent.startServices()
}
