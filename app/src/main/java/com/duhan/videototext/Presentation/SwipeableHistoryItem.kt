package com.duhan.videototext.Presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.duhan.videototext.Data.LocalDataSource.SearchHistoryModel
import com.duhan.videototext.ui.theme.ErrorRed
import com.duhan.videototext.ui.theme.PrimaryOrange
import com.duhan.videototext.ui.theme.SurfaceWhite
import com.duhan.videototext.ui.theme.TextBlack
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwipeableHistoryItem(
    historyItem: SearchHistoryModel,
    onItemClick: (SearchHistoryModel) -> Unit,
    onYoutubeIconClick: (String) -> Unit,
    onDeleteClick: (SearchHistoryModel) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }

    val youtubeRevealWidth = with(LocalDensity.current) { 80.dp.toPx() }
    val deleteRevealWidth = with(LocalDensity.current) { -80.dp.toPx() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                when {
                    offsetX.value > 0 -> Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF4facfe).copy(alpha = 0.3f),
                            Color.White
                        )
                    )
                    offsetX.value < 0 -> Brush.horizontalGradient(
                        colors = listOf(
                            Color.White,
                            ErrorRed.copy(alpha = 0.6f)
                        )
                    )
                    else -> Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, Color.Transparent)
                    )
                }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    onYoutubeIconClick(historyItem.searchQuery)
                    coroutineScope.launch { offsetX.animateTo(0f) }
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircleOutline,
                    contentDescription = "Open in YouTube",
                    tint = PrimaryOrange,
                    modifier = Modifier.size(35.dp)
                )
            }
            IconButton(
                onClick = {
                    onDeleteClick(historyItem)
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Item",
                    tint = ErrorRed,
                    modifier = Modifier.size(35.dp)
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .border(
                    width = 1.dp,
                    color = Color.LightGray.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(14.dp)
                )
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            coroutineScope.launch {
                                val newOffset = (offsetX.value + dragAmount)
                                    .coerceIn(deleteRevealWidth, youtubeRevealWidth)
                                offsetX.snapTo(newOffset)
                            }
                        },
                        onDragEnd = {
                            coroutineScope.launch {
                                val currentOffset = offsetX.value
                                val youtubeThreshold = youtubeRevealWidth * 0.5f
                                val deleteThreshold = deleteRevealWidth * 0.5f

                                when {
                                    currentOffset > youtubeThreshold -> {
                                        offsetX.animateTo(youtubeRevealWidth, animationSpec = tween(300))
                                    }
                                    currentOffset < deleteThreshold -> {
                                        offsetX.animateTo(deleteRevealWidth, animationSpec = tween(300))
                                    }
                                    else -> {
                                        offsetX.animateTo(0f, animationSpec = tween(300))
                                    }
                                }
                            }
                        }
                    )
                }
                .clickable {
                    coroutineScope.launch {
                        if (offsetX.value != 0f) {
                            offsetX.animateTo(0f, animationSpec = tween(300))
                        } else {
                            onItemClick(historyItem)
                        }
                    }
                },
            color = SurfaceWhite,
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = historyItem.searchQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = TextBlack,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}