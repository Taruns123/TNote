package com.example.noter

import android.app.Application
import com.example.noter.demo.DemoNotes
import com.example.noter.feature_note.domain.repository.NoteRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class NoteApp : Application() {

    @Inject lateinit var repository: NoteRepository

    override fun onCreate() {
        super.onCreate()
        // Debug builds seed a handful of sample notes on first launch; release builds do nothing.
        DemoNotes.seedIfFirstLaunch(this, repository)
    }
}
