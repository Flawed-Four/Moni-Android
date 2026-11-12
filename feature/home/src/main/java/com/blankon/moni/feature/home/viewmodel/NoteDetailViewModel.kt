package com.blankon.moni.feature.home.viewmodel


import androidx.lifecycle.viewModelScope
import com.blankon.moni.core.common.base.BaseViewModel
import com.blankon.moni.core.data.model.local.ListItemEntity
import com.blankon.moni.core.data.model.local.NoteEntity
import com.blankon.moni.core.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(NoteDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun load(noteId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { notesRepository.getNoteWithItems(noteId) }
                .onSuccess { result ->
                    _uiState.value = if (result == null) {
                        NoteDetailUiState(error = "Note tidak ditemukan")
                    } else {
                        NoteDetailUiState(
                            note = result.note,
                            items = result.items.sortedBy { it.position })
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
        }
    }

    fun delete(onDeleted: () -> Unit) {
        val id = _uiState.value.note?.id ?: return
        viewModelScope.launch {
            runCatching { notesRepository.deleteNote(id) }
                .onSuccess { onDeleted() }
                .onFailure { error -> _uiState.update { it.copy(error = error.message) } }
        }
    }
}

data class NoteDetailUiState(
    val note: NoteEntity? = null,
    val items: List<ListItemEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
