package chatter.lib.log

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity

// write to stdout with fancy colors
//
// otherwise do exactly the to the same as the `CommonWriter` that
// is the default logger
object StdoutLogWriter : LogWriter() {
    override fun log(
        severity: Severity,
        message: String,
        tag: String,
        throwable: Throwable?
    ) {
        message.lines().forEach {
            val severityText = severity.toLogText()

            println("[${severityText} ${tag}] $it")
        }
        throwable?.printStackTrace()
    }

    private fun Severity.toLogText(): String {
        val color = when (this) {
            Severity.Verbose -> TextColor.White
            Severity.Debug -> TextColor.Cyan
            Severity.Info -> TextColor.Green
            Severity.Warn -> TextColor.Yellow
            Severity.Error -> TextColor.Red
            Severity.Assert -> TextColor.Red
        }

        return color.withColor(toString().uppercase())
    }
}

private enum class TextColor(private val code: Int) {
    Red(31), Yellow(32), Cyan(36), White(37), Green(32);


    fun withColor(text: String) = "\u001B[0${code}m${text}\u001B[0m"
}
