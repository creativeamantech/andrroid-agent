package com.mahavtaar.droidagent.core.tools

sealed class ToolResult {
    abstract val success: Boolean
    abstract fun toObservation(): String

    data class Success(val output: String) : ToolResult() {
        override val success = true
        override fun toObservation() = output
    }
    data class Failure(val error: String) : ToolResult() {
        override val success = false
        override fun toObservation() = error
    }
}
