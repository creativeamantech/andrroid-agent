package com.mahavtaar.droidagent.core.agent
import com.mahavtaar.droidagent.core.llm.LLMEngine
import com.mahavtaar.droidagent.core.memory.ExperienceLogger
import com.mahavtaar.droidagent.core.memory.MemoryRetriever
import com.mahavtaar.droidagent.core.skills.SkillManager
import com.mahavtaar.droidagent.core.tools.ToolExecutor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
open class AgentOrchestrator @Inject constructor(
    private val llmEngine: LLMEngine,
    private val toolExecutor: ToolExecutor,
    private val memoryRetriever: MemoryRetriever,
    private val experienceLogger: ExperienceLogger,
    private val skillManager: SkillManager,
    private val agentPlanner: AgentPlanner
) {
    private val _agentState = MutableStateFlow<AgentState>(AgentState.Idle)
    val agentState: StateFlow<AgentState> = _agentState.asStateFlow()
    private val _steps = MutableStateFlow<List<AgentStep>>(emptyList())
    val steps: StateFlow<List<AgentStep>> = _steps.asStateFlow()

    suspend fun executeGoal(goal: String, maxSteps: Int = 20) {
        val sessionId = UUID.randomUUID().toString()
        val conversation = mutableListOf<Message>()
        val plan = agentPlanner.plan(goal)
        conversation.add(Message.User(plan))

        var stepCount = 0
        var isComplete = false
        _agentState.emit(AgentState.Thinking)

        while (!isComplete && stepCount < maxSteps) {
            stepCount++
            var output = ""
            llmEngine.stream("", conversation).collect { output += it }

            val thought = extractBlock(output, "thought")
            val actionJson = extractBlock(output, "action")
            val finalAnswer = extractBlock(output, "final_answer")

            if (finalAnswer != null) {
                _agentState.emit(AgentState.Done(finalAnswer))
                isComplete = true
                break
            }

            if (actionJson != null) {
                _agentState.emit(AgentState.Acting)
                val toolResult = toolExecutor.execute(actionJson)
                _steps.value = _steps.value + AgentStep.Action(actionJson, toolResult.toObservation(), toolResult.success)
                conversation.add(Message.Assistant(output))
                conversation.add(Message.User(toolResult.toObservation()))
            } else {
                conversation.add(Message.User("Error"))
            }
        }
        if (!isComplete) _agentState.emit(AgentState.Error("Max steps reached without completing goal."))
    }

    private fun extractBlock(text: String, blockName: String): String? {
        val regex = "<$blockName>([\\s\\S]*?)</$blockName>".toRegex(RegexOption.IGNORE_CASE)
        return regex.find(text)?.groupValues?.get(1)?.trim()
    }
}
