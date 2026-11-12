package com.blankon.moni.feature.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.blankon.moni.core.common.base.BaseScreen
import com.blankon.moni.core.data.model.local.NoteEntity
import com.blankon.moni.core.data.model.local.NoteType
import com.blankon.moni.feature.home.viewmodel.HomeViewModel

private data class NoteFilter(val label: String, val count: Int)

private val TextPrimary = Color(0xFF202020)
private val TextSecondary = Color(0xFF4B4B4B)
private val PrimaryGreen = Color(0xFFD9FA92)
private val NoteColors = listOf(
    Color(0xFFF1F1F1), Color(0xFFFBEFF0), Color(0xFFEDF4FC),
    Color(0xFFFCF6E1), Color(0xFFEDF7F0)
)

@Composable
internal fun HomeLandingScreen(
    onAddNote: () -> Unit = {},
    onAIClick: () -> Unit = {},
    onNoteClick: (String) -> Unit = {},
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All Notes") }
    val uiState by viewModel.uiState.collectAsState()

    val filteredNotes = remember(uiState.notes, searchQuery, selectedCategory) {
        uiState.notes.filter { note ->
            val matchesCategory = when (selectedCategory) {
                "Text" -> note.type == NoteType.TEXT
                "Lists" -> note.type == NoteType.LIST
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() ||
                    note.title.contains(searchQuery, ignoreCase = true) ||
                    note.body.orEmpty().contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    val filters = listOf(
        NoteFilter("All Notes", uiState.notes.size),
        NoteFilter("Text", uiState.textCount),
        NoteFilter("Lists", uiState.listCount)
    )
    val leftNotes = filteredNotes.filterIndexed { index, _ -> index % 2 == 0 }
    val rightNotes = filteredNotes.filterIndexed { index, _ -> index % 2 != 0 }

    BaseScreen(
        modifier = Modifier.fillMaxSize(),
        showDefaultTopBar = false,
        // AppNavHost already applies systemBarsPadding to the NavHost.
        systemBarsPadding = false
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                HomeSearchBar(value = searchQuery, onValueChange = { searchQuery = it })
                Spacer(Modifier.height(16.dp))
                CategorySelector(
                    categories = filters,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
                Spacer(Modifier.height(14.dp))

                if (uiState.isLoading) {
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Loading notes...", color = TextSecondary)
                    }
                } else if (filteredNotes.isEmpty()) {
                    Box(
                        Modifier.weight(1f).fillMaxWidth().padding(bottom = 72.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                if (searchQuery.isBlank()) "No notes yet" else "No notes found",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                if (searchQuery.isBlank()) "Create your first note to get started." else "Try another search keyword.",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(bottom = 88.dp)
                        ) {
                            items(leftNotes, key = { it.id }) { note ->
                                NoteCard(
                                    note = note,
                                    color = NoteColors[uiState.notes.indexOf(note).coerceAtLeast(0) % NoteColors.size],
                                    onClick = { onNoteClick(note.id) }
                                )
                            }
                        }
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(bottom = 88.dp)
                        ) {
                            items(rightNotes, key = { it.id }) { note ->
                                NoteCard(
                                    note = note,
                                    color = NoteColors[uiState.notes.indexOf(note).coerceAtLeast(0) % NoteColors.size],
                                    onClick = { onNoteClick(note.id) }
                                )
                            }
                        }
                    }
                }
            }

            HomeFloatingActions(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp),
                onAddNote = onAddNote,
                onAIClick = onAIClick
            )
        }
    }
}

@Composable
private fun HomeSearchBar(value: String, onValueChange: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Row(
            modifier = Modifier.weight(1f).height(42.dp)
                .clip(RoundedCornerShape(30.dp))
                .border(1.dp, Color(0xFFCCCCCC), RoundedCornerShape(30.dp))
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = TextPrimary, modifier = Modifier.size(21.dp))
            Spacer(Modifier.width(10.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = TextPrimary),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) Text("Find your notes", fontSize = 14.sp, color = TextSecondary)
                        innerTextField()
                    }
                }
            )
        }
        Spacer(Modifier.width(12.dp))
        Box(
            modifier = Modifier.size(42.dp).clip(CircleShape).background(Color(0xFFEAEAEA)),
            contentAlignment = Alignment.Center
        ) { Text("👨🏻‍💻", fontSize = 25.sp) }
    }
}

@Composable
private fun CategorySelector(
    categories: List<NoteFilter>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            val selected = selectedCategory == category.label
            Row(
                modifier = Modifier.clip(CircleShape)
                    .background(if (selected) PrimaryGreen else Color(0xFFEEEEEE))
                    .clickable { onCategorySelected(category.label) }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(category.label, fontSize = 13.sp, color = TextPrimary)
                Spacer(Modifier.width(7.dp))
                Box(
                    modifier = Modifier.clip(CircleShape)
                        .background(if (selected) Color(0xFF538B13) else Color(0xFFE0E0E0))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        category.count.toString(), fontSize = 10.sp,
                        color = if (selected) Color.White else Color.DarkGray
                    )
                }
            }
        }
    }
}

@Composable
private fun NoteCard(note: NoteEntity, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = note.title,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(12.dp), tint = TextSecondary)
                Spacer(Modifier.width(4.dp))
                Text(formatNoteDate(note.updatedAt), fontSize = 11.sp, color = TextSecondary)
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = note.body.orEmpty().ifBlank { if (note.type == NoteType.LIST) "Checklist" else "No content" },
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = TextSecondary,
                maxLines = 8,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(shape = CircleShape, color = Color(0xFFE5E5E5)) {
                    Text(
                        text = if (note.type == NoteType.LIST) "List" else "${note.body.orEmpty().wordsCount()} Words",
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        fontSize = 11.sp,
                        color = TextPrimary
                    )
                }
                Icon(Icons.Default.MoreHoriz, contentDescription = "More options", tint = Color.Black, modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
private fun HomeFloatingActions(
    modifier: Modifier = Modifier,
    onAddNote: () -> Unit,
    onAIClick: () -> Unit
) {
    Row(
        modifier = modifier.clip(CircleShape).background(Color.White)
            .border(1.dp, Color(0xFFE5E5E5), CircleShape).padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        FloatingActionButton(
            onClick = onAddNote,
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            containerColor = Color.Black,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add note")
        }
        FloatingActionButton(
            onClick = onAIClick,
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            containerColor = PrimaryGreen,
            contentColor = Color(0xFF182600)
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = "AI Assistant")
        }
    }
}

private fun String.wordsCount(): Int = trim().split(Regex("\\s+")).count { it.isNotBlank() }

private fun formatNoteDate(timestamp: Long): String {
    val formatter = java.text.SimpleDateFormat("dd MMM, HH:mm", java.util.Locale.getDefault())
    return formatter.format(java.util.Date(timestamp))
}
