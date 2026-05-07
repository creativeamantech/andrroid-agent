package com.mahavtaar.droidagent.core.memory
import com.mahavtaar.droidagent.data.db.dao.MemoryDao
import com.mahavtaar.droidagent.data.db.entities.MemoryEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
data class Memory(val id: String = UUID.randomUUID().toString(), val content: String, val embedding: FloatArray, val tags: List<String>, val timestamp: Long = System.currentTimeMillis(), val accessCount: Int = 0, val outcomeSuccess: Boolean = true)
@Singleton
open class MemoryStore @Inject constructor(private val memoryDao: MemoryDao) {
    open suspend fun save(memory: Memory) {
        val jsonArrayStr = Json.encodeToString(memory.embedding.toList())
        val entity = MemoryEntity(memory.id, memory.content, jsonArrayStr, memory.tags.joinToString(","), memory.timestamp, memory.accessCount, memory.outcomeSuccess)
        memoryDao.insertMemory(entity)
    }
    open suspend fun getAllMemories(): List<Memory> = memoryDao.getAllMemories().map { toMemory(it) }
    private fun toMemory(entity: MemoryEntity): Memory {
        val floatList = Json.decodeFromString<List<Float>>(entity.embeddingJson)
        val embedding = floatList.toFloatArray()
        return Memory(entity.id, entity.content, embedding, entity.tags.split(",").filter { it.isNotBlank() }, entity.timestamp, entity.accessCount, entity.outcomeSuccess)
    }
}
