package com.example.noter.feature_note.presentation.notes.components

import com.example.noter.feature_note.domain.model.Note
import com.example.noter.feature_note.domain.util.NoteOrder
import com.example.noter.feature_note.domain.util.OrderType

data class NotesState(
    /** Notes after search + colour filter are applied. */
    val notes: List<Note> = emptyList(),
    /** Total number of notes in the database (before filtering). */
    val totalCount: Int = 0,
    val noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending),
    val isOrderSectionVisible: Boolean = false,
    val searchQuery: String = "",
    val colorFilter: Int? = null,
    val isLoaded: Boolean = false,
)
