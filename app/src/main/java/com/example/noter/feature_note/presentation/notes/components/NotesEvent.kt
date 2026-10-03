package com.example.noter.feature_note.presentation.notes.components

import com.example.noter.feature_note.domain.model.Note
import com.example.noter.feature_note.domain.util.NoteOrder

sealed class NotesEvent {
    data class Order(val noteOrder: NoteOrder) : NotesEvent()
    data class DeleteNote(val note: Note) : NotesEvent()
    data class Search(val query: String) : NotesEvent()
    data class FilterColor(val color: Int?) : NotesEvent()
    object RestoreNote : NotesEvent()
    object ToggleOrderSection : NotesEvent()
}
