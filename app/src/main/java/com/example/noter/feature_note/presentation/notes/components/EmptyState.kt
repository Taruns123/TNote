package com.example.noter.feature_note.presentation.notes.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.noter.ui.theme.NoteTones
import com.example.noter.ui.theme.card

/** Canvas-drawn illustration of three fanned note cards, plus a message. */
@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    val tones = NoteTones.all
    val back = tones[3].card()
    val middle = tones[1].card()
    val front = tones[0].card()
    val dot = tones[0].dot
    val line = MaterialTheme.colorScheme.outlineVariant
    val outline = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)

    Column(
        modifier = modifier.padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Canvas(modifier = Modifier.size(width = 170.dp, height = 150.dp)) {
            val w = 96.dp.toPx()
            val h = 118.dp.toPx()
            val r = CornerRadius(16.dp.toPx())
            val cx = size.width / 2f
            val top = (size.height - h) / 2f
            rotate(-12f, pivot = Offset(cx, size.height)) {
                drawRoundRect(back, Offset(cx - w / 2, top), Size(w, h), r)
                drawRoundRect(outline, Offset(cx - w / 2, top), Size(w, h), r, style = Stroke(1.dp.toPx()))
            }
            rotate(9f, pivot = Offset(cx, size.height)) {
                drawRoundRect(middle, Offset(cx - w / 2, top), Size(w, h), r)
                drawRoundRect(outline, Offset(cx - w / 2, top), Size(w, h), r, style = Stroke(1.dp.toPx()))
            }
            drawRoundRect(front, Offset(cx - w / 2, top), Size(w, h), r)
            drawRoundRect(outline, Offset(cx - w / 2, top), Size(w, h), r, style = Stroke(1.dp.toPx()))
            // Ruled lines on the front card
            val left = cx - w / 2 + 16.dp.toPx()
            val lineH = 4.dp.toPx()
            drawRoundRect(dot, Offset(left, top + 20.dp.toPx()), Size(w * 0.45f, lineH * 1.4f), CornerRadius(lineH))
            listOf(0.68f, 0.56f, 0.64f, 0.38f).forEachIndexed { i, f ->
                drawRoundRect(
                    line,
                    Offset(left, top + 42.dp.toPx() + i * 15.dp.toPx()),
                    Size(w * f, lineH),
                    CornerRadius(lineH)
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(
            title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
