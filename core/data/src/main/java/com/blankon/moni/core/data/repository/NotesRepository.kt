package com.blankon.moni.core.data.repository

import com.blankon.moni.core.data.model.local.NoteEntity
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun observeNotes(): Flow<List<NoteEntity>>
    fun searchNotes(query: String): Flow<List<NoteEntity>>
    suspend fun getNoteById(id: String): NoteEntity?
    suspend fun saveNote(note: NoteEntity)
    suspend fun deleteNote(id: String)
}
