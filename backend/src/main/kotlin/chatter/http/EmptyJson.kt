package chatter.http

import kotlinx.serialization.Serializable

// for compatibility for the api we return a empty json object
// when we return nothing for the abi
//
// this class represents such empty json object. `Unit` would
// have also worked but this looks better and everyone should know what this means
@Serializable
object EmptyJson

