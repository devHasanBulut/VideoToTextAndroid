package com.duhan.videototext.Presentation

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duhan.videototext.ui.theme.PrimaryOrange
import com.duhan.videototext.ui.theme.TextBlack
import com.duhan.videototext.ui.theme.TextGray

@Composable
fun ModernBottomBar(
    summary: com.duhan.videototext.Data.LocalDataSource.SummaryTextModel?,
    clipboardManager: androidx.compose.ui.platform.ClipboardManager,
    context: android.content.Context,
    onCategoryClick: () -> Unit
) {
    BottomAppBar(
        containerColor = Color.White,
        contentColor = TextGray,
        modifier = Modifier
            .shadow(12.dp, spotColor = Color.Black.copy(alpha = 0.1f))
            .navigationBarsPadding()
            .height(80.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 8.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EnhancedBottomActionItem(
                text = "Kopyala",
                icon = Icons.Default.ContentCopy,
                onClick = {
                    summary?.let {
                        clipboardManager.setText(AnnotatedString(it.summaryText))
                        Toast.makeText(context, "Panoya kopyalandı", Toast.LENGTH_SHORT).show()
                    }
                }
            )
            EnhancedBottomActionItem(
                text = "Paylaş",
                icon = Icons.Default.Share,
                onClick = {
                    summary?.let {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, it.summaryText)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }
                }
            )
            EnhancedBottomActionItem(
                text = "Kategoriye Ekle",
                icon = Icons.Default.LibraryAdd,
                onClick = onCategoryClick
            )
        }
    }
}

@Composable
fun EnhancedBottomActionItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "bottomActionScale"
    )

    Column(
        modifier = Modifier
            .scale(scale)
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            )
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (isPressed) {
                        PrimaryOrange.copy(alpha = 0.1f)
                    } else {
                        Color.Transparent
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = text,
                tint = if (isPressed) PrimaryOrange else TextBlack,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            color = if (isPressed) PrimaryOrange else TextGray,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            fontSize = 11.sp
        )
    }
}
