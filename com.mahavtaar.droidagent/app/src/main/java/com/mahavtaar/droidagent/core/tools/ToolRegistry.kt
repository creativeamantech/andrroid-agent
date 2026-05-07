package com.mahavtaar.droidagent.core.tools
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
open class ToolRegistry @Inject constructor() {
    private val tools = mutableMapOf<String, BaseTool>()
    fun register(tool: BaseTool) {
        val ann = tool::class.annotations.filterIsInstance<Tool>().firstOrNull() ?: return
        tools[ann.name] = tool
    }
    open fun getTool(name: String): BaseTool? = tools[name]
}
