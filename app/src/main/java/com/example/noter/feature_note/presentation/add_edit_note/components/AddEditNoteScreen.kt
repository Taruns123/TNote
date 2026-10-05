package com.example.noter.feature_note.presentation.add_edit_note.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.noter.feature_note.presentation.util.TimeFormat
import com.example.noter.ui.theme.NoteTones
import com.example.noter.ui.theme.card
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    navController: NavController,
    noteColor: Int,
    viewModel: AddEditNoteViewModel = hiltViewModel()
) {
    val titleState = viewModel.noteTitle.value
    val contentState = viewModel.noteContent.value
    val timestamp = viewModel.noteTimestamp.value
    val snackbarHostState = remember { SnackbarHostState() }

    // Nav argument gives the right colour instantly for existing notes (no flash).
    var selectedColor by remember {
        mutableIntStateOf(if (noteColor != -1) noteColor else viewModel.noteColor.value)
    }
    val tone = NoteTones.forArgb(selectedColor)
    val background by animateColorAsState(tone.card(), tween(450), label = "noteBackground")

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddEditNoteViewModel.UiEvent.ShowSnackbar ->
                    snackbarHostState.showSnackbar(message = event.message)
                is AddEditNoteViewModel.UiEvent.SaveNote -> navController.navigateUp()
            }
        }
    }

    val words = contentState.text.trim().split(Regex("\\s+")).count { it.isNotBlank() }

    Scaffold(
        containerColor = background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleAction(Icons.AutoMirrored.Outlined.ArrowBack, "Back") {
                    navController.navigateUp()
                }
                Spacer(Modifier.weight(1f))
                if (timestamp != null) {
                    CircleAction(Icons.Outlined.DeleteOutline, "Delete note") {
                        viewModel.onEvent(AddEditNoteEvent.DeleteNote)
                    }
                    Spacer(Modifier.width(10.dp))
                }
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.onBackground)
                        .clickable { viewModel.onEvent(AddEditNoteEvent.SaveNote) }
                        .padding(start = 14.dp, end = 18.dp, top = 11.dp, bottom = 11.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Check, contentDescription = null,
                        tint = MaterialTheme.colorScheme.background,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Save",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.background
                    )
                }
            }
        },
        bottomBar = {
            ColorPalette(
                selected = selectedColor,
                onSelect = {
                    selectedColor = it
                    viewModel.onEvent(AddEditNoteEvent.ChangeColor(it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(tone.dot)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = buildString {
                        append(if (timestamp != null) "Edited ${TimeFormat.full(timestamp)}" else "New note")
                        if (words > 0) append("  ·  $words ${if (words == 1) "word" else "words"}")
                    }.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(14.dp))
            TransparentHintTextField(
                text = titleState.text,
                hint = titleState.hint,
                onValueChange = { viewModel.onEvent(AddEditNoteEvent.EnteredTitle(it)) },
                onFocusChange = { viewModel.onEvent(AddEditNoteEvent.ChangeTitleFocus(it)) },
                isHintVisible = titleState.isHintVisible,
                textStyle = MaterialTheme.typography.headlineLarge
            )
            Spacer(Modifier.height(16.dp))
            Box(
                Modifier
                    .width(40.dp)
                    .height(2.dp)
                    .background(tone.dot.copy(alpha = 0.6f))
            )
            Spacer(Modifier.height(16.dp))
            TransparentHintTextField(
                text = contentState.text,
                hint = contentState.hint,
                onValueChange = { viewModel.onEvent(AddEditNoteEvent.EnteredContent(it)) },
                onFocusChange = { viewModel.onEvent(AddEditNoteEvent.ChangeContentFocus(it)) },
                isHintVisible = contentState.isHintVisible,
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 320.dp)
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CircleAction(icon: ImageVector, description: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.06f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = description, tint = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun ColorPalette(selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(26.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.92f))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(26.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NoteTones.all.forEach { tone ->
            val argb = tone.light.toArgb()
            val isSelected = selected == argb
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .border(
                        2.dp,
                        if (isSelected) MaterialTheme.colorScheme.onBackground else Color.Transparent,
                        CircleShape
                    )
                    .clickable { onSelect(argb) }
                    .padding(5.dp)
                    .clip(CircleShape)
                    .background(tone.card())
                    .border(6.dp, tone.dot, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        Icons.Outlined.Check,
                        contentDescription = tone.name,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
