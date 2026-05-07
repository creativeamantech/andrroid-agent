package com.mahavtaar.droidagent.core.tools

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ToolExecutorTest {

    private class DummyTool : BaseTool() {
        override suspend fun execute(params: Map<String, Any>): ToolResult {
            val name = params["name"] as? String ?: "Unknown"
            return ToolResult.Success("Hello $name")
        }
    }

    private class DummyToolRegistry : ToolRegistry() {
        private val tool = DummyTool()
        override fun getTool(name: String): BaseTool? {
            return if (name == "dummy_tool") tool else null
        }
    }

    @Test
    fun testExecuteValidTool() = runBlocking {
        val executor = ToolExecutor(DummyToolRegistry())
        val actionJson = """
            {
                "tool": "dummy_tool",
                "params": {
                    "name": "World"
                }
            }
        """.trimIndent()

        val result = executor.execute(actionJson)
        assertTrue(result is ToolResult.Success)
        assertEquals("Hello World", (result as ToolResult.Success).output)
    }

    @Test
    fun testExecuteUnknownTool() = runBlocking {
        val executor = ToolExecutor(DummyToolRegistry())
        val actionJson = """
            {
                "tool": "non_existent_tool",
                "params": {}
            }
        """.trimIndent()

        val result = executor.execute(actionJson)
        assertTrue(result is ToolResult.Failure)
    }

    @Test
    fun testExecuteInvalidJson() = runBlocking {
        val executor = ToolExecutor(DummyToolRegistry())
        val actionJson = "{"

        val result = executor.execute(actionJson)
        assertTrue(result is ToolResult.Failure)
    }
}
