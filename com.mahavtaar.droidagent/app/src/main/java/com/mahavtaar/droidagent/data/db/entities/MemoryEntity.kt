package com.mahavtaar.droidagent.data.db.entities
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey val id: String,
    val content: String,
    val embeddingJson: String,
    val tags: String,
    val timestamp: Long,
    val accessCount: Int = 0,
    val outcomeSuccess: Boolean = true
)
