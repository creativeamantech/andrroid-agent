package com.mahavtaar.droidagent.core.memory
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt
@Singleton
open class MemoryRetriever @Inject constructor(private val memoryStore: MemoryStore) {
    open suspend fun retrieve(query: String, topK: Int): List<String> = memoryStore.getAllMemories().map { it.content }.take(topK)
}
