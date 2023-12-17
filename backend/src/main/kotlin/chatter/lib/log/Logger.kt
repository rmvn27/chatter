package chatter.lib.log

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import kotlin.reflect.KProperty

// create the logger by delegate which uses the class name as the tag
// of the class where it is used
operator fun Logger.Companion.getValue(
    ref: Any,
    property: KProperty<*>
): Logger {
    val name = ref.javaClass.simpleName
        ?: error("Can't use Logger delegate on an anonymous clas")

    return Logger.withTag(name)
}

fun Logger.Companion.setSeverityFromEnv() {
    val rawSeverity = System.getenv("CHATTER_LOG_MIN_SEVERITY") ?: "Info"
    val severity = Severity.valueOf(rawSeverity)

    setMinSeverity(severity)
}
