package com.duhan.videototext.Presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.duhan.videototext.ui.theme.PrimaryOrange
import com.duhan.videototext.ui.theme.TextBlack

@Composable
fun AddCategoryCard(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(100.dp)
            .height(160.dp)
            .background(Color.White, RoundedCornerShape(32.dp))
            .border(1.dp, PrimaryOrange.copy(alpha = 0.5f), RoundedCornerShape(32.dp))
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(PrimaryOrange.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = PrimaryOrange)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Ekle", color = PrimaryOrange, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CategoryCard(
    title: String,
    iconColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(160.dp)
            .background(backgroundColor, RoundedCornerShape(32.dp))
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder icon
                Icon(
                    imageVector = if (title == "Favorites") Icons.Default.AccessTime else Icons.Default.Folder,
                    contentDescription = null,
                    tint = iconColor
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    ),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = TextBlack,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}