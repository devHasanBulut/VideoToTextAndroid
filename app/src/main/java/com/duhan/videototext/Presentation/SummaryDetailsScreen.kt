package com.duhan.videototext.Presentation

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.duhan.videototext.ui.theme.*

private fun calculateReadingTime(text: String): Int {
    val wordsPerMinute = 200
    val numberOfWords = text.split(Regex("\\s+")).size
    val minutes = numberOfWords / wordsPerMinute
    return if (minutes > 0) minutes else 1
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryDetailScreen(
    summaryId: Int,
    mainActivityViewModel: MainActivityViewModel,
    navController: NavController
) {
    val summary by mainActivityViewModel.selectedSummary.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var showCategoryDialog by remember { mutableStateOf(false) }


    LaunchedEffect(key1 = summaryId) {
        mainActivityViewModel.getSummaryById(summaryId)
    }
    
    if (showCategoryDialog && summary != null) {
        CategorySelectionDialog(
            viewModel = mainActivityViewModel,
            summaryId = summary!!.id,
            onDismiss = { showCategoryDialog = false },
            onCategorySelected = { categoryId ->
                mainActivityViewModel.assignSummaryToCategory(
                    summaryId = summary!!.id,
                    categoryId = categoryId
                )
                showCategoryDialog = false
            }
        )
    }
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 4.dp)) {
                        ModernBackButton(onClick = { navController.popBackStack() })
                    }
                },
                actions = {
                    val isFavorite by mainActivityViewModel.isFavorite.collectAsState()
                    IconButton(
                        onClick = {
                            summary?.let {
                                mainActivityViewModel.toggleFavorite(it.id)
                                val message = if (isFavorite) "Favorilerden çıkarıldı" else "Favorilere eklendi"
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = if (isFavorite) "Favorilerden çıkar" else "Favorilere ekle",
                            tint = PrimaryOrange
                        )
                    }
                    IconButton(
                        onClick = {
                            summary?.url?.let { url ->
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }
                        }
                    ) {
                        Icon(Icons.Default.OndemandVideo, "YouTube'da aç", tint = PrimaryOrange)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },

        bottomBar = {
            ModernBottomBar(
                summary = summary,
                clipboardManager = clipboardManager,
                context = context,
                onCategoryClick = { showCategoryDialog = true }
            )
        }
    ) { innerPadding ->
        if (summary == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    ,
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(
                        color = PrimaryOrange,
                        modifier = Modifier.size(48.dp)
                    )
                    Text("Yükleniyor...", color = TextGray)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                
                // Title Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(24.dp),
                            spotColor = Color(0xFF667eea).copy(alpha = 0.2f)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(width = 1.dp, color = Color(0xFFEEEEEE), shape = RoundedCornerShape(24.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .height(32.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(PrimaryOrange)
                                )
                                
                                Text(
                                    text = summary!!.videoTitle ?: "Video Özeti",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = TextBlack,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 32.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            val readingTime = calculateReadingTime(summary!!.summaryText)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryOrange)
                                )
                                Text(
                                    text = "Tahmini okuma süresi: $readingTime dk",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextGray,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(24.dp),
                            spotColor = Color(0xFF4facfe).copy(alpha = 0.1f)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp, 
                                color = Color(0xFFEEEEEE), 
                                shape = RoundedCornerShape(24.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                           Color(0xFFE3F2FD)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.radialGradient(
                                                    colors = listOf(
                                                        Color(0xFF4facfe),
                                                        Color(0xFF00f2fe)
                                                    )
                                                )
                                            )
                                    )
                                }
                                
                                Text(
                                    text = "Özet İçeriği",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = TextBlack,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            Text(
                                text = summary!!.summaryText,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextBlack,
                                lineHeight = 30.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}




