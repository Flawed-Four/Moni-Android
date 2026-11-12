package com.blankon.moni.feature.home.screen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.blankon.moni.core.common.base.BaseScreen
import com.blankon.moni.core.data.model.local.NoteType
import com.blankon.moni.feature.home.viewmodel.NoteDetailViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun NoteDetailScreen(
    noteId: String,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDeleted: () -> Unit,
    viewModel: NoteDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    LaunchedEffect(noteId) { viewModel.load(noteId) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete note?") },
            text = { Text("Catatan ini akan dihapus dari daftar. Aksi ini tidak dapat dibatalkan dari aplikasi.") },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; viewModel.delete(onDeleted) }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                }) { Text("Cancel") }
            }
        )
    }

    BaseScreen(
        modifier = Modifier.fillMaxSize(),
        showDefaultTopBar = false,
        systemBarsPadding = false
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Row {
                    IconButton(onClick = onEdit, enabled = state.note != null) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit note")
                    }
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        enabled = state.note != null
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete note")
                    }
                }
            }
            when {
                state.isLoading -> Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) { CircularProgressIndicator() }

                state.note == null -> Text(
                    state.error ?: "Note tidak ditemukan",
                    modifier = Modifier.padding(16.dp)
                )

                else -> Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 24.dp)
                ) {
                    Text(state.note!!.title, fontSize = 27.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Updated ${
                            SimpleDateFormat(
                                "dd MMM yyyy, HH:mm",
                                Locale.getDefault()
                            ).format(Date(state.note!!.updatedAt))
                        }", fontSize = 12.sp
                    )
                    Spacer(Modifier.height(20.dp))
                    if (state.note!!.type == NoteType.LIST) {
                        state.items.forEach { item ->
                            Text(
                                "${if (item.checked) "☑" else "☐"}  ${item.name}",
                                modifier = Modifier.padding(vertical = 7.dp),
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        Text(state.note!!.body.orEmpty(), fontSize = 16.sp, lineHeight = 25.sp)
                    }
                    state.error?.let {
                        Text(
                            it,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
