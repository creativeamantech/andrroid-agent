package com.mahavtaar.droidagent.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mahavtaar.droidagent.data.db.entities.MemoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity)

    @Query("SELECT * FROM memories")
    suspend fun getAllMemories(): List<MemoryEntity>

    @Query("SELECT * FROM memories ORDER BY timestamp DESC")
    fun observeMemories(): Flow<List<MemoryEntity>>
}
