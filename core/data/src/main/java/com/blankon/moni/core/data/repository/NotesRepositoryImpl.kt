package com.blankon.moni.core.data.repository


import com.blankon.moni.core.data.model.local.NoteEntity
import com.blankon.moni.core.data.source.local.dao.NoteDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NotesRepository {

    override fun observeNotes(): Flow<List<NoteEntity>> = noteDao.observeNotes()

    override fun searchNotes(query: String): Flow<List<NoteEntity>> =
        noteDao.searchNotes(query.trim())

    override suspend fun getNoteById(id: String): NoteEntity? = noteDao.getNoteById(id)

    override suspend fun getNoteWithItems(id: String) = noteDao.getNoteWithItems(id)

    override suspend fun saveNote(note: NoteEntity) {
        noteDao.upsert(note)
    }

    override suspend fun deleteNote(id: String) {
        val now = System.currentTimeMillis()
        noteDao.softDelete(id = id, deletedAt = now, updatedAt = now)
    }
}
