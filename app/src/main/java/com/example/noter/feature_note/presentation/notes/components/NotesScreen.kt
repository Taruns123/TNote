package com.example.noter.feature_note.presentation.notes.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.noter.feature_note.domain.util.NoteOrder
import com.example.noter.feature_note.presentation.util.Screen
import com.example.noter.feature_note.presentation.util.TimeFormat
import com.example.noter.ui.theme.NoteTones
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@ExperimentalAnimationApi
@Composable
fun NotesScreen(
    navController: NavController,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Screen.AddEditNoteScreen.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(20.dp),
                icon = { Icon(Icons.Outlined.Edit, contentDescription = null) },
                text = { Text("New note", style = MaterialTheme.typography.labelLarge) }
            )
        }
    ) { innerPadding ->
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp, top = 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 96.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalItemSpacing = 12.dp
        ) {
            item(span = StaggeredGridItemSpan.FullLine, key = "header") {
                Header(
                    count = if (state.searchQuery.isNotBlank() || state.colorFilter != null)
                        "${state.notes.size} of ${state.totalCount}" else "${state.totalCount}",
                    onSortClick = { viewModel.onEvent(NotesEvent.ToggleOrderSection) },
                    sortLabel = sortLabel(state.noteOrder)
                )
            }
            item(span = StaggeredGridItemSpan.FullLine, key = "search") {
                SearchField(
                    query = state.searchQuery,
                    onQueryChange = { viewModel.onEvent(NotesEvent.Search(it)) }
                )
            }
            item(span = StaggeredGridItemSpan.FullLine, key = "filters") {
                ColorFilterRow(
                    selected = state.colorFilter,
                    onSelect = { viewModel.onEvent(NotesEvent.FilterColor(it)) }
                )
            }

            if (state.isLoaded && state.notes.isEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine, key = "empty") {
                    val filtering = state.searchQuery.isNotBlank() || state.colorFilter != null
                    EmptyState(
                        title = if (filtering) "Nothing matches" else "A blank page",
                        message = when {
                            state.searchQuery.isNotBlank() ->
                                "No notes contain “${state.searchQuery.trim()}”. Try another word or clear the filters."
                            filtering -> "No notes in this colour yet."
                            else -> "Your thoughts deserve a home. Tap “New note” to write the first one."
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 56.dp)
                    )
                }
            }

            items(state.notes, key = { it.id ?: it.hashCode() }) { note ->
                NoteItem(
                    note = note,
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItemPlacement(),
                    onClick = {
                        navController.navigate(
                            Screen.AddEditNoteScreen.route +
                                "?noteId=${note.id}&noteColor=${note.color}"
                        )
                    },
                    onLongClick = {
                        viewModel.onEvent(NotesEvent.DeleteNote(note))
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Note deleted",
                                actionLabel = "Undo"
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.onEvent(NotesEvent.RestoreNote)
                            }
                        }
                    }
                )
            }
        }
    }

    if (state.isOrderSectionVisible) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onEvent(NotesEvent.ToggleOrderSection) },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            windowInsets = WindowInsets(0, 0, 0, 0),
        ) {
            OrderSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
                    .navigationBarsPadding(),
                noteOrder = state.noteOrder,
                onOrderChange = { viewModel.onEvent(NotesEvent.Order(it)) }
            )
        }
    }
}

private fun sortLabel(order: NoteOrder): String = when (order) {
    is NoteOrder.Date -> "Last edited"
    is NoteOrder.Title -> "Title"
    is NoteOrder.Color -> "Colour"
}

@Composable
private fun Header(count: String, sortLabel: String, onSortClick: () -> Unit) {
    Column(Modifier.padding(start = 4.dp, top = 12.dp, bottom = 4.dp)) {
        Text(
            TimeFormat.today().uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                "Notes",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.width(10.dp))
            Text(
                count,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .clip(RoundedCornerShape(50))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(50))
                    .clickable(onClick = onSortClick)
                    .padding(start = 12.dp, end = 14.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.Sort,
                    contentDescription = "Sort notes",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    sortLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .height(46.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Outlined.Search, contentDescription = null,
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(Modifier.width(12.dp))
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (query.isEmpty()) {
                Text(
                    "Search your notes",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (query.isNotEmpty()) {
            IconButton(onClick = { onQueryChange("") }, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Outlined.Close, contentDescription = "Clear search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ColorFilterRow(selected: Int?, onSelect: (Int?) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        item {
            FilterPill(label = "All", dot = null, selected = selected == null) { onSelect(null) }
        }
        items(NoteTones.all) { tone ->
            val argb = tone.light.toArgb()
            FilterPill(label = tone.name, dot = tone.dot, selected = selected == argb) {
                onSelect(argb)
            }
        }
    }
}

@Composable
private fun FilterPill(label: String, dot: Color?, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .clip(shape)
            .background(if (selected) colors.onBackground else Color.Transparent)
            .border(1.dp, if (selected) colors.onBackground else colors.outlineVariant, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (dot != null) {
            Box(
                Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(dot)
            )
            Spacer(Modifier.width(7.dp))
        }
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) colors.background else colors.onSurfaceVariant
        )
    }
}
