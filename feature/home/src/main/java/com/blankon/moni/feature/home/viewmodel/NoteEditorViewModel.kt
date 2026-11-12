package com.blankon.moni.feature.home.viewmodel


import androidx.lifecycle.viewModelScope
import com.blankon.moni.core.common.base.BaseViewModel
import com.blankon.moni.core.data.model.local.NoteEntity
import com.blankon.moni.core.data.model.local.NoteType
import com.blankon.moni.core.data.model.local.SyncState
import com.blankon.moni.core.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(NoteEditorUiState())
    val uiState = _uiState.asStateFlow()

    fun loadNote(noteId: String?) {
        if (noteId.isNullOrBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching { notesRepository.getNoteById(noteId) }
                .onSuccess { note ->
                    if (note != null) {
                        _uiState.value = NoteEditorUiState(
                            noteId = note.id,
                            title = note.title,
                            body = note.body.orEmpty(),
                            existingNote = note,
                            isLoading = false
                        )
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Note tidak ditemukan"
                            )
                        }
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun updateTitle(value: String) = _uiState.update { it.copy(title = value) }
    fun updateBody(value: String) = _uiState.update { it.copy(body = value) }

    fun save(onSaved: () -> Unit) {
        val current = _uiState.value
        val title = current.title.trim().ifBlank {
            current.body.lineSequence().firstOrNull().orEmpty().take(60).ifBlank { "Untitled" }
        }
        if (current.body.isBlank() && current.title.isBlank()) {
            _uiState.update { it.copy(error = "Tambahkan judul atau isi catatan terlebih dahulu") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            val now = System.currentTimeMillis()
            val note = current.existingNote?.copy(
                title = title,
                body = current.body.trim(),
                updatedAt = now,
                syncState = SyncState.PENDING
            ) ?: NoteEntity(
                id = UUID.randomUUID().toString(),
                type = NoteType.TEXT,
                title = title,
                body = current.body.trim(),
                createdAt = now,
                updatedAt = now,
                syncState = SyncState.PENDING,
                clientRequestId = UUID.randomUUID().toString()
            )

            runCatching { notesRepository.saveNote(note) }
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, error = null) }
                    onSaved()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isSaving = false, error = error.message) }
                }
        }
    }
}

data class NoteEditorUiState(
    val noteId: String? = null,
    val existingNote: NoteEntity? = null,
    val title: String = "",
    val body: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)
