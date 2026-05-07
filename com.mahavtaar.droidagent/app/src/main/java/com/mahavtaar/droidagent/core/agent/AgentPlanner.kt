package com.mahavtaar.droidagent.core.agent
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
open class AgentPlanner @Inject constructor() {
    open suspend fun plan(goal: String): String = "1. Plan task: $goal"
}
