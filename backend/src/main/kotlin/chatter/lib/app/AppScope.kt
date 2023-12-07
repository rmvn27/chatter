package chatter.lib.app

// injection scope that is used across the whole app
// to connect the `AppScope` with the different modules
// and bound services
//
// this also allows to define singletons in the injection
// so some dependencies (especially if they are stateful)
// are not created twice
object AppScope
