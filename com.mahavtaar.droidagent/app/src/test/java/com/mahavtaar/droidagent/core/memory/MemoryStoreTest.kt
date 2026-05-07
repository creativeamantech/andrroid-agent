package com.mahavtaar.droidagent.core.memory

import com.mahavtaar.droidagent.data.db.dao.MemoryDao
import com.mahavtaar.droidagent.data.db.entities.MemoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MemoryStoreTest {

    private class MockMemoryDao : MemoryDao {
        val memoryDb = mutableListOf<MemoryEntity>()

        override suspend fun insertMemory(memory: MemoryEntity) {
            memoryDb.add(memory)
        }

        override suspend fun getAllMemories(): List<MemoryEntity> {
            return memoryDb
        }

        override fun observeMemories(): Flow<List<MemoryEntity>> {
            return flowOf(memoryDb)
        }
    }

    @Test
    fun testSaveAndRetrieveMemory() = runBlocking {
        val dao = MockMemoryDao()
        val store = MemoryStore(dao)

        val memory = Memory(
            content = "Test content",
            embedding = floatArrayOf(0.1f, 0.2f, 0.3f),
            tags = listOf("test", "dummy")
        )

        store.save(memory)

        val retrieved = store.getAllMemories()
        assertEquals(1, retrieved.size)

        val retrievedMemory = retrieved[0]
        assertEquals("Test content", retrievedMemory.content)
        assertTrue(retrievedMemory.tags.contains("test"))
        assertTrue(retrievedMemory.tags.contains("dummy"))
        assertEquals(3, retrievedMemory.embedding.size)
        assertEquals(0.1f, retrievedMemory.embedding[0], 0.001f)
    }
}
