package com.duhan.videototext.Presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.duhan.videototext.ui.theme.TextBlack
import com.duhan.videototext.ui.theme.TextGrayLight

@Composable
fun ModernBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "backButtonScale"
    )
    
    Box(
        modifier = modifier
            .size(44.dp)
            .scale(scale)
            .shadow(
                elevation = if (isPressed) 1.dp else 4.dp,
                shape = CircleShape,
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .clip(CircleShape)
            .background(Color.White)
            .border(
                width = 1.dp,
                color = TextGrayLight.copy(alpha = 0.3f),
                shape = CircleShape
            )
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Geri",
            tint = TextBlack,
            modifier = Modifier.size(20.dp)
        )
    }
}

