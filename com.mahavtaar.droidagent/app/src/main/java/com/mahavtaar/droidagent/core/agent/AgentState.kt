package com.mahavtaar.droidagent.core.agent
sealed class AgentState {
    object Idle : AgentState()
    object Thinking : AgentState()
    object Acting : AgentState()
    object Observing : AgentState()
    data class Done(val result: String) : AgentState()
    data class Error(val message: String) : AgentState()
}
sealed class Message {
    data class User(val content: String) : Message()
    data class Assistant(val content: String) : Message()
}
sealed class AgentStep {
    data class Thinking(val thought: String) : AgentStep()
    data class Action(val actionJson: String, val observation: String, val success: Boolean) : AgentStep()
    fun toJson(): String = when (this) {
        is Thinking -> "{\"thought\": \"${thought.replace("\"", "\\\"")}\"}"
        is Action -> "{\"action\": $actionJson, \"observation\": \"${observation.replace("\"", "\\\"")}\", \"success\": $success}"
    }
}
