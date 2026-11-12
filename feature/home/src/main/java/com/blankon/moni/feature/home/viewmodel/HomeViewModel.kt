package com.blankon.moni.feature.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.blankon.moni.core.common.base.BaseViewModel
import com.blankon.moni.core.data.model.local.NoteEntity
import com.blankon.moni.core.data.model.local.NoteType
import com.blankon.moni.core.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeNotes()
    }

    private fun observeNotes() {
        viewModelScope.launch {
            notesRepository.observeNotes()
                .catch { error -> _uiState.update { it.copy(error = error.message) } }
                .collect { notes ->
                    _uiState.update { it.copy(notes = notes, isLoading = false, error = null) }
                }
        }
    }

    fun deleteNote(id: String) {
        viewModelScope.launch {
            runCatching { notesRepository.deleteNote(id) }
                .onFailure { error -> _uiState.update { it.copy(error = error.message) } }
        }
    }
}

data class HomeUiState(
    val notes: List<NoteEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
) {
    val textCount: Int get() = notes.count { it.type == NoteType.TEXT }
    val listCount: Int get() = notes.count { it.type == NoteType.LIST }
}
