package com.example.noter.feature_note.presentation.notes.components

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noter.feature_note.domain.model.Note
import com.example.noter.feature_note.domain.use_case.NoteUseCase
import com.example.noter.feature_note.domain.util.NoteOrder
import com.example.noter.feature_note.domain.util.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteUseCase: NoteUseCase
) : ViewModel() {

    private val _state = mutableStateOf(NotesState())
    val state: State<NotesState> = _state

    /** Sorted notes as delivered by the use case, before search/filter. */
    private var sortedNotes: List<Note> = emptyList()

    private var recentlyDeletedNote: Note? = null
    private var getNotesJob: Job? = null

    init {
        getNotes(NoteOrder.Date(OrderType.Descending))
    }

    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.Order -> {
                if (state.value.noteOrder::class == event.noteOrder::class &&
                    state.value.noteOrder.orderType == event.noteOrder.orderType
                ) return
                getNotes(event.noteOrder)
            }
            is NotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    noteUseCase.deleteNote(event.note)
                    recentlyDeletedNote = event.note
                }
            }
            is NotesEvent.RestoreNote -> {
                viewModelScope.launch {
                    noteUseCase.addNote(recentlyDeletedNote ?: return@launch)
                    recentlyDeletedNote = null
                }
            }
            is NotesEvent.ToggleOrderSection -> {
                _state.value = state.value.copy(
                    isOrderSectionVisible = !state.value.isOrderSectionVisible
                )
            }
            is NotesEvent.Search -> {
                _state.value = state.value.copy(searchQuery = event.query)
                publish()
            }
            is NotesEvent.FilterColor -> {
                _state.value = state.value.copy(
                    colorFilter = if (state.value.colorFilter == event.color) null else event.color
                )
                publish()
            }
        }
    }

    private fun getNotes(noteOrder: NoteOrder) {
        getNotesJob?.cancel()
        getNotesJob = noteUseCase.getNotes(noteOrder)
            .onEach { notes ->
                sortedNotes = notes
                _state.value = state.value.copy(noteOrder = noteOrder, isLoaded = true)
                publish()
            }
            .launchIn(viewModelScope)
    }

    private fun publish() {
        val query = state.value.searchQuery.trim()
        val color = state.value.colorFilter
        val visible = sortedNotes.filter { note ->
            (color == null || note.color == color) &&
                (query.isEmpty() ||
                    note.title.contains(query, ignoreCase = true) ||
                    note.content.contains(query, ignoreCase = true))
        }
        _state.value = state.value.copy(notes = visible, totalCount = sortedNotes.size)
    }
}
