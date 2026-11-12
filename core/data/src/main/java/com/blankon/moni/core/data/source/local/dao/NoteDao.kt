package com.blankon.moni.core.data.source.local.dao


import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.blankon.moni.core.data.model.local.NoteEntity
import com.blankon.moni.core.data.model.local.NoteWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes WHERE deleted_at IS NULL ORDER BY updated_at DESC")
    fun observeNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id AND deleted_at IS NULL LIMIT 1")
    suspend fun getNoteById(id: String): NoteEntity?

    @Transaction
    @Query("SELECT * FROM notes WHERE id = :id AND deleted_at IS NULL LIMIT 1")
    suspend fun getNoteWithItems(id: String): NoteWithItems?

    @Upsert
    suspend fun upsert(note: NoteEntity)

    @Update
    suspend fun update(note: NoteEntity)

    @Query("UPDATE notes SET deleted_at = :deletedAt, updated_at = :updatedAt, sync_state = 'PENDING' WHERE id = :id")
    suspend fun softDelete(id: String, deletedAt: Long, updatedAt: Long)

    @Query("SELECT * FROM notes WHERE deleted_at IS NULL AND (title LIKE '%' || :query || '%' OR body LIKE '%' || :query || '%') ORDER BY updated_at DESC")
    fun searchNotes(query: String): Flow<List<NoteEntity>>
}
