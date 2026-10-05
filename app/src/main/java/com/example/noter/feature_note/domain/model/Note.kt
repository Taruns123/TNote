package com.example.noter.feature_note.domain.model

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.noter.ui.theme.NoteTones


@Entity
data class Note(
    val title: String,
    val content: String,
    val timestamp : Long,
    val color: Int,
    @PrimaryKey val id: Int? = null

){
    companion object{
        val noteColors = NoteTones.all.map { it.light }
    }
}

class InvalidNoteException(message: String): Exception(message)
