package com.mahavtaar.droidagent.core.tools

abstract class BaseTool {
    abstract suspend fun execute(params: Map<String, Any>): ToolResult
}
