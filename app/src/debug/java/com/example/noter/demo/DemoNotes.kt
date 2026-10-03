package com.example.noter.demo

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import com.example.noter.feature_note.domain.model.Note
import com.example.noter.feature_note.domain.repository.NoteRepository
import com.example.noter.ui.theme.NoteTones
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/** Debug-only: seeds realistic sample notes once, so the UI can be demoed. */
object DemoNotes {
    private const val PREFS = "demo_seed"
    private const val KEY = "seeded_v1"

    private const val MIN = 60_000L
    private const val HOUR = 60 * MIN
    private const val DAY = 24 * HOUR

    fun seedIfFirstLaunch(context: Context, repository: NoteRepository) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (prefs.getBoolean(KEY, false)) return
        prefs.edit().putBoolean(KEY, true).apply()

        val now = System.currentTimeMillis()
        val (sage, butter, clay, lavender, sky) = NoteTones.all.map { it.light.toArgb() }
        val rose = NoteTones.all[5].light.toArgb()

        val notes = listOf(
            Note(
                "Weekend groceries",
                "Sourdough loaf\nOat milk ×2\nCherry tomatoes\nFeta + fresh basil\nLemons\nGreek yogurt\nCoffee beans — the Ethiopian roast",
                now - 35 * MIN, sage
            ),
            Note(
                "Q4 roadmap sync",
                "With Priya, Marcus, Elena\n\n• Ship offline mode before 15 Nov\n• Analytics dashboard moves to Q1\n• Marcus drafts the onboarding emails\n• Next sync Thursday, 10:00",
                now - 3 * HOUR, sky
            ),
            Note(
                "Call mom",
                "Sunday evening. Ask how the knee appointment went.",
                now - 5 * HOUR, rose
            ),
            Note(
                "On starting",
                "“The scariest moment is always just before you start.”\n\n— Stephen King, On Writing",
                now - 1 * DAY - 2 * HOUR, butter
            ),
            Note(
                "Lisbon, 4 days",
                "Day 1 — Alfama, sunset at Miradouro da Senhora do Monte\nDay 2 — Belém: tower, monastery, pastéis de nata\nDay 3 — Sintra day trip (book Pena Palace!)\nDay 4 — LX Factory, Time Out Market, fly home 21:40",
                now - 2 * DAY, clay
            ),
            Note(
                "App idea: plant swap",
                "A neighbourhood app for trading cuttings. Map of nearby swaps, a care card for every plant, reputation from successful trades. Launch in one city first.",
                now - 3 * DAY, lavender
            ),
            Note(
                "Reading list",
                "Tomorrow, and Tomorrow, and Tomorrow\nThe Overstory\nFour Thousand Weeks\nPiranesi",
                now - 4 * DAY, sage
            ),
            Note(
                "Simplicity",
                "“Simplicity is the ultimate sophistication.” Remove one thing from every screen.",
                now - 5 * DAY, rose
            ),
            Note(
                "Training week",
                "Mon — push\nWed — pull\nFri — legs + core\nDaily: 20 min walk after lunch",
                now - 6 * DAY, sky
            ),
            Note(
                "Gift ideas",
                "Anika — ceramic pour-over set\nDad — noise-cancelling earbuds\nRavi — a monstera from the plant shop",
                now - 9 * DAY, butter
            ),
        )

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            notes.forEach { repository.insertNote(it) }
        }
    }
}
