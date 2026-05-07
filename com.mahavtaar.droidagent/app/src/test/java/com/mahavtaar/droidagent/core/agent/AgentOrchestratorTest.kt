package com.mahavtaar.droidagent.core.agent

import com.mahavtaar.droidagent.core.llm.LLMEngine
import com.mahavtaar.droidagent.core.memory.ExperienceLogger
import com.mahavtaar.droidagent.core.memory.MemoryRetriever
import com.mahavtaar.droidagent.core.skills.SkillManager
import com.mahavtaar.droidagent.core.tools.ToolExecutor
import com.mahavtaar.droidagent.core.tools.ToolRegistry
import com.mahavtaar.droidagent.core.tools.ToolResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock

class AgentOrchestratorTest {

    private class MockLLMEngine(private val responses: List<String>) : LLMEngine {
        override val name = "MockEngine"
        override val isAvailable = true
        override val maxContextTokens = 1000
        private var responseIndex = 0

        override fun stream(
            systemPrompt: String,
            messages: List<Message>,
            temperature: Float,
            topK: Int,
            topP: Float
        ): Flow<String> {
            val response = if (responseIndex < responses.size) responses[responseIndex++] else "<final_answer>Default complete.</final_answer>"
            return flowOf(response)
        }

        override suspend fun generate(systemPrompt: String, messages: List<Message>, maxTokens: Int): String {
            return if (responseIndex < responses.size) responses[responseIndex++] else "<final_answer>Default complete.</final_answer>"
        }

        override suspend fun getEmbedding(text: String) = FloatArray(0)
    }

    private class MockToolExecutor : ToolExecutor(ToolRegistry()) {
        override suspend fun execute(actionJson: String): ToolResult {
            return ToolResult.Success("Tool executed successfully.")
        }
    }

    private class MockMemoryRetriever : MemoryRetriever(mock(com.mahavtaar.droidagent.core.memory.MemoryStore::class.java)) {
        override suspend fun retrieve(query: String, topK: Int): List<String> = emptyList()
    }

    private class MockExperienceLogger : ExperienceLogger() {
        override fun logStep(sessionId: String, thought: String?, actionJson: String?, observation: String?) {}
        override fun logSuccess(sessionId: String, goal: String, steps: List<AgentStep>) {}
    }

    private class MockSkillManager : SkillManager() {
        override fun getActiveSkillsPrompt() = ""
    }

    private class MockAgentPlanner : AgentPlanner() {
        override suspend fun plan(goal: String) = "Mock Plan"
    }

    @Test
    fun testSuccessfulExecution() = runBlocking {
        val mockResponses = listOf(
            "<thought>I should do an action</thought>\n<action>{\"tool\": \"test_tool\"}</action>",
            "<thought>I have finished</thought>\n<final_answer>Done with the task</final_answer>"
        )

        val llmEngine = MockLLMEngine(mockResponses)

        val orchestrator = AgentOrchestrator(
            llmEngine,
            MockToolExecutor(),
            MockMemoryRetriever(),
            MockExperienceLogger(),
            MockSkillManager(),
            MockAgentPlanner()
        )

        orchestrator.executeGoal("Test Goal", 5)

        assertTrue(orchestrator.agentState.value is AgentState.Done)
        assertEquals("Done with the task", (orchestrator.agentState.value as AgentState.Done).result)
    }

    @Test
    fun testMaxStepsReached() = runBlocking {
        // Continuous non-finishing responses
        val mockResponses = List(10) {
            "<thought>Still working</thought>\n<action>{\"tool\": \"test_tool\"}</action>"
        }

        val llmEngine = MockLLMEngine(mockResponses)

        val orchestrator = AgentOrchestrator(
            llmEngine,
            MockToolExecutor(),
            MockMemoryRetriever(),
            MockExperienceLogger(),
            MockSkillManager(),
            MockAgentPlanner()
        )

        orchestrator.executeGoal("Test Goal", maxSteps = 3)

        assertTrue(orchestrator.agentState.value is AgentState.Error)
        assertEquals("Max steps reached without completing goal.", (orchestrator.agentState.value as AgentState.Error).message)
    }
}
