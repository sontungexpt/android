package com.comic.android_native_client.ui.activities.index.screens.reading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun Header(
    chapterName: String,
    modifier: Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Previous Chapter Button
        IconButton(
            onClick = { /* Handle Previous Chapter */ },
            modifier = Modifier
                .size(48.dp)  // Larger buttons for better tap area
                .padding(start = 12.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(28.dp),
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Previous Chapter",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        // Chapter Name in the Center with ellipsis for overflow
        TextButton(
            modifier = Modifier.weight(1f),  // This ensures the text button takes up available space
            onClick = { /* Handle Chapter Selection */ }
        ) {
            Text(
                text = chapterName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,  // Ellipsis for long chapter names
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        // Next Chapter Button
        IconButton(
            onClick = { /* Handle Next Chapter */ },
            modifier = Modifier
                .size(48.dp)  // Larger buttons for better tap area
                .padding(end = 12.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(28.dp),
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next Chapter",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}