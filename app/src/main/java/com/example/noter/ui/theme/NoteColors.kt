package com.example.noter.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/** A note colour family: light card, dark card and a saturated accent dot. */
data class NoteTone(val name: String, val light: Color, val dark: Color, val dot: Color)

object NoteTones {
    val all = listOf(
        NoteTone("Sage", NoteSage, NoteSageDark, DotSage),
        NoteTone("Butter", NoteButter, NoteButterDark, DotButter),
        NoteTone("Clay", NoteClay, NoteClayDark, DotClay),
        NoteTone("Lavender", NoteLavender, NoteLavenderDark, DotLavender),
        NoteTone("Sky", NoteSky, NoteSkyDark, DotSky),
        NoteTone("Rose", NoteRose, NoteRoseDark, DotRose),
    )

    /** Find the tone for a stored ARGB value (falls back gracefully for legacy colours). */
    fun forArgb(argb: Int): NoteTone {
        all.firstOrNull { it.light.value == Color(argb).value }?.let { return it }
        val c = Color(argb)
        return NoteTone("Custom", c, c.copy(alpha = 1f).darken(), c)
    }

    private fun Color.darken(): Color = Color(red * 0.25f, green * 0.25f, blue * 0.25f, 1f)
}

/** Card colour for the current theme. */
@Composable
@ReadOnlyComposable
fun NoteTone.card(): Color =
    if (MaterialTheme.colorScheme.background.luminance() < 0.5f) dark else light
