package com.duhan.videototext.Presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomSheetDefaults.DragHandle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duhan.videototext.ui.theme.PrimaryOrange
import com.duhan.videototext.ui.theme.TextBlack
import com.duhan.videototext.ui.theme.TextGray
import com.duhan.videototext.ui.theme.TextGrayLight
import kotlin.math.abs

private data class SummaryTier(
    val id: String,
    val title: String,
    val subtitle: String,
    val ratio: Int,
    val isPremium: Boolean,
    val isRecommended: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryDialog(
    initialRatio: Int,
    isPremiumUser: Boolean,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit,
    onShowPaywall: () -> Unit
) {
    val tiers = remember {
        listOf(
            SummaryTier("quick", "Hızlı Bakış", "Ücretsiz | ~%10 Özet", 10, isPremium = false),
            SummaryTier("detailed", "Detaylı Özet", "Premium | ~%30 Özet", 30, isPremium = true),
            SummaryTier("in-depth", "Derinlemesine Analiz", "Premium | ~%50 Özet", 50, isPremium = true),
            SummaryTier("smart", "Akıllı Notlar", "Premium Plus | ~%70 Özet", 70, isPremium = true, isRecommended = true)
        )
    }
    
    val savedTier = remember(initialRatio) {
        tiers.minByOrNull { abs(it.ratio - initialRatio) } ?: tiers.first()
    }

    val activeTier = remember(savedTier, isPremiumUser) {
        if (!isPremiumUser && savedTier.isPremium) {
            tiers.first { !it.isPremium }
        } else {
            savedTier
        }
    }

    var selectedTier by remember { mutableStateOf(activeTier) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = { DragHandle(color = Color.LightGray) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Özet Seviyesi Seç",
                style = MaterialTheme.typography.headlineSmall,
                color = TextBlack,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                tiers.forEach { tier ->
                    ModernSelectableTierRow(
                        title = tier.title,
                        subtitle = tier.subtitle,
                        isRecommended = tier.isRecommended,
                        isSelected = (tier.id == selectedTier.id),
                        isPremium = tier.isPremium,
                        isLocked = tier.isPremium && !isPremiumUser,
                        onClick = {
                            if (tier.isPremium && !isPremiumUser) {
                                onShowPaywall()
                            } else {
                                selectedTier = tier
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onSave(selectedTier.ratio)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(14.dp), spotColor = PrimaryOrange.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
            ) {
                Text(
                    "Özetle", 
                    color = Color.White, 
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernSelectableTierRow(
    title: String,
    subtitle: String,
    isRecommended: Boolean,
    isSelected: Boolean,
    isPremium: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    val borderColor = when {
        isSelected -> PrimaryOrange
        isLocked -> Color.LightGray.copy(alpha = 0.5f)
        else -> Color.LightGray
    }
    
    val containerColor = when {
        isSelected -> PrimaryOrange.copy(alpha = 0.05f)
        isLocked -> Color(0xFFFAFAFA) // Very light gray for locked
        else -> Color.White
    }

    Box {
        if (isRecommended) {
            Badge(
                containerColor = PrimaryOrange,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Text(
                    "ÖNERİLEN",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(14.dp))
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            title,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isLocked) TextGray else TextBlack,
                            fontWeight = FontWeight.Bold
                        )
                        
                        if (isLocked) {
                            Text(
                                "🔒",
                                fontSize = 14.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(PrimaryOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    RadioButton(
                        selected = false,
                        onClick = null,
                        colors = RadioButtonDefaults.colors(
                            unselectedColor = if (isLocked) Color.LightGray else TextGray
                        )
                    )
                }
            }
        }
    }
}


