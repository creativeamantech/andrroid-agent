package com.mahavtaar.droidagent.core.llm

import com.mahavtaar.droidagent.core.agent.Message
import kotlinx.coroutines.flow.Flow

interface LLMEngine {
    val name: String
    val isAvailable: Boolean
    val maxContextTokens: Int

    fun stream(
        systemPrompt: String,
        messages: List<Message>,
        temperature: Float = 0.7f,
        topK: Int = 40,
        topP: Float = 0.9f
    ): Flow<String>

    suspend fun generate(
        systemPrompt: String,
        messages: List<Message>,
        maxTokens: Int = 2048
    ): String

    suspend fun getEmbedding(text: String): FloatArray
}
