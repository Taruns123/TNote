package com.example.noter.feature_note.presentation.notes.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.noter.feature_note.domain.model.Note
import com.example.noter.feature_note.presentation.util.TimeFormat
import com.example.noter.ui.theme.NoteTones
import com.example.noter.ui.theme.card

/**
 * A note card drawn on a Canvas: rounded paper with a folded top-right corner
 * tinted by the note's accent colour.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItem(
    note: Note,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    foldSize: Dp = 26.dp,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val tone = NoteTones.forArgb(note.color)
    val cardColor = tone.card()
    val foldColor = tone.dot.copy(alpha = 0.32f).compositeOver(cardColor)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val fold = foldSize.toPx()
            val radius = CornerRadius(cornerRadius.toPx())
            val paper = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width - fold, 0f)
                lineTo(size.width, fold)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            clipPath(paper) {
                drawRoundRect(color = cardColor, size = size, cornerRadius = radius)
                // The folded-over flap
                drawRoundRect(
                    color = foldColor,
                    topLeft = Offset(size.width - fold, -fold),
                    size = Size(fold * 2, fold * 2),
                    cornerRadius = CornerRadius(fold * 0.45f)
                )
            }
        }

        Column(modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 16.dp)) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 14.dp)
            )
            if (note.content.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 9,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(tone.dot)
                )
                Spacer(Modifier.width(7.dp))
                Text(
                    text = TimeFormat.relative(note.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
