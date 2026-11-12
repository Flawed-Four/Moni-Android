package com.blankon.moni.core.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.blankon.moni.core.data.model.local.ListItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ListItemDao {

    @Query("SELECT * FROM list_items WHERE note_id = :noteId ORDER BY position ASC")
    fun observeItems(noteId: String): Flow<List<ListItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ListItemEntity>)

    @Update
    suspend fun update(item: ListItemEntity)

    @Query("DELETE FROM list_items WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM list_items WHERE note_id = :noteId")
    suspend fun deleteByNoteId(noteId: String)
}
