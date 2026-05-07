package com.mahavtaar.droidagent.core.memory
import com.mahavtaar.droidagent.core.agent.AgentStep
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
open class ExperienceLogger @Inject constructor() {
    open fun logStep(sessionId: String, thought: String?, actionJson: String?, observation: String?) {}
    open fun logSuccess(sessionId: String, goal: String, steps: List<AgentStep>) {}
}
