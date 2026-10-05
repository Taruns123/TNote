package com.example.noter.demo

import android.content.Context
import com.example.noter.feature_note.domain.repository.NoteRepository

/** No-op in release builds: real users start with an empty notebook. */
object DemoNotes {
    @Suppress("UNUSED_PARAMETER")
    fun seedIfFirstLaunch(context: Context, repository: NoteRepository) = Unit
}
