package chatter.lib

import java.util.UUID

fun String.toUUID(): UUID = UUID.fromString(this)
