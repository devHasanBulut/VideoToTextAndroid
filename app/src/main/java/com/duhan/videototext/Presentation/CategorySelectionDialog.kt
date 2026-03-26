package com.duhan.videototext.Presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duhan.videototext.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectionDialog(
    viewModel: MainActivityViewModel,
    summaryId: Int,
    onDismiss: () -> Unit,
    onCategorySelected: (Int) -> Unit
) {
    var newCategoryName by remember { mutableStateOf("") }
    var showNewCategoryInput by remember { mutableStateOf(false) }
    
    val allCategories by viewModel.categoryList.collectAsState()
    val categories = allCategories.filter {
        it.id != MainActivityViewModel.DownloadsCategoryId
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = { 
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(48.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(Color.LightGray.copy(alpha = 0.5f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Modern Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(PrimaryOrange.copy(alpha = 0.6f), PrimaryOrange)
                            )
                        )
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    "Kategori Seç",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextBlack,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(PrimaryOrange.copy(alpha = 0.6f), PrimaryOrange)
                            )
                        )
                )
            }
            
            // Divider
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                PrimaryOrange,
                                PrimaryOrange.copy(alpha = 0.5f),
                                PrimaryOrange,
                                Color.Transparent
                            )
                        )
                    )
            )
            
            Spacer(modifier = Modifier.height(28.dp))

            if (categories.isNotEmpty() && !showNewCategoryInput) {
                // Section Title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(20.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(PrimaryOrange)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        "Mevcut Kategoriler",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextGray,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(categories) { index, category ->
                        ModernCategoryItem(
                            categoryName = category.categoryName,
                            index = index,
                            onClick = { onCategorySelected(category.id) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.LightGray,
                                    Color.LightGray.copy(alpha = 0.5f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Create New Category
            if (!showNewCategoryInput) {
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.97f else 1f,
                    animationSpec = spring(),
                    label = "buttonScale"
                )
                
                Button(
                    onClick = { showNewCategoryInput = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp)
                        .scale(scale),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    interactionSource = interactionSource
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(62.dp)
                            .background(
                                color = BackgroundLight,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = PrimaryOrange.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryOrange.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = PrimaryOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Text(
                                "Yeni Kategori Oluştur",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextBlack,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.3.sp
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(20.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(SuccessGreen)
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            "Yeni Kategori",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextGray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    // TextField
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        label = { 
                            Text(
                                "Kategori Adı", 
                                color = TextGray,
                                style = MaterialTheme.typography.bodyMedium
                            ) 
                        },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryOrange.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.FolderOpen,
                                    contentDescription = null,
                                    tint = PrimaryOrange,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = TextBlack,
                            unfocusedTextColor = TextBlack,
                            focusedIndicatorColor = PrimaryOrange,
                            unfocusedIndicatorColor = Color.LightGray,
                            cursorColor = PrimaryOrange,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedLabelColor = PrimaryOrange,
                            unfocusedLabelColor = TextGray
                        )
                    )
                    
                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Cancel
                        val cancelInteractionSource = remember { MutableInteractionSource() }
                        val isCancelPressed by cancelInteractionSource.collectIsPressedAsState()
                        val cancelScale by animateFloatAsState(
                            targetValue = if (isCancelPressed) 0.95f else 1f,
                            animationSpec = spring(),
                            label = "cancelScale"
                        )
                        
                        Button(
                            onClick = { 
                                showNewCategoryInput = false
                                newCategoryName = ""
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .scale(cancelScale),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            interactionSource = cancelInteractionSource
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .background(
                                        Color.White,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = Color.LightGray,
                                        shape = RoundedCornerShape(14.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "İptal",
                                    color = TextGray,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        
                        // Create
                        val createInteractionSource = remember { MutableInteractionSource() }
                        val isCreatePressed by createInteractionSource.collectIsPressedAsState()
                        val createScale by animateFloatAsState(
                            targetValue = if (isCreatePressed) 0.95f else 1f,
                            animationSpec = spring(),
                            label = "createScale"
                        )
                        
                        Button(
                            onClick = {
                                if (newCategoryName.isNotBlank()) {
                                    viewModel.addNewCategoryAndAssignToSummary(
                                        categoryName = newCategoryName.trim(),
                                        summaryId = summaryId
                                    )
                                    onDismiss()
                                }
                            },
                            enabled = newCategoryName.isNotBlank(),
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .scale(createScale),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent
                            ),
                            interactionSource = createInteractionSource
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .background(
                                        if (newCategoryName.isNotBlank()) {
                                            PrimaryOrange
                                        } else {
                                            Color.LightGray
                                        },
                                        shape = RoundedCornerShape(14.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    Text(
                                        "Oluştur",
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}