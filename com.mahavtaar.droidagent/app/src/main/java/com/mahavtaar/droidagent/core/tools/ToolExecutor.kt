package com.mahavtaar.droidagent.core.tools
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class ToolExecutor @Inject constructor(private val registry: ToolRegistry) {
    open suspend fun execute(actionJson: String): ToolResult {
        return try {
            val json = Json.parseToJsonElement(actionJson) as JsonObject
            val toolName = json["tool"]?.jsonPrimitive?.contentOrNull
            if (toolName.isNullOrBlank()) return ToolResult.Failure("Missing tool name")

            val tool = registry.getTool(toolName) ?: return ToolResult.Failure("Tool not found")
            val paramsObj = json["params"] as? JsonObject
            val params = mutableMapOf<String, Any>()

            paramsObj?.forEach { (k, v) ->
                params[k] = v.jsonPrimitive.contentOrNull ?: v.jsonPrimitive.content
            }

            tool.execute(params)
        } catch (e: Exception) { ToolResult.Failure(e.message ?: "Execution error") }
    }
}
