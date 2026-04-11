package com.ksetrasevakah.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vector_documents",
    indices = [Index(value = ["createdAt"])]
)
data class VectorDocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val embedding: ByteArray,
    val metadataJson: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VectorDocumentEntity) return false
        return id == other.id &&
            text == other.text &&
            embedding.contentEquals(other.embedding) &&
            metadataJson == other.metadataJson &&
            createdAt == other.createdAt
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + embedding.contentHashCode()
        result = 31 * result + metadataJson.hashCode()
        result = 31 * result + createdAt.hashCode()
        return result
    }
}
