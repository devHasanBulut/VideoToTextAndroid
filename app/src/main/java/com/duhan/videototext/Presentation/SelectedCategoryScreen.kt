package com.duhan.videototext.Presentation.SelectedCategoryScreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.duhan.videototext.Presentation.MainActivityViewModel
import com.duhan.videototext.Presentation.ModernBackButton
import com.duhan.videototext.ui.theme.BackgroundLight
import com.duhan.videototext.ui.theme.ErrorRed
import com.duhan.videototext.ui.theme.PrimaryOrange
import com.duhan.videototext.ui.theme.PrimaryOrangeDark
import com.duhan.videototext.ui.theme.SurfaceWhite
import com.duhan.videototext.ui.theme.TextBlack
import com.duhan.videototext.ui.theme.TextGray

data class VideoUiModel(
    val id: Int,
    val thumbnailUrl: String?,
    val title: String,
    val summarySnippet: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    categoryId: Int,
    categoryName: String,
    mainActivityViewModel: MainActivityViewModel,
    navController: NavController
) {
    val videoList by mainActivityViewModel.videosForCategoryUi.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var summaryToDelete by remember { mutableStateOf<VideoUiModel?>(null) }

    LaunchedEffect(key1 = categoryId) {
        mainActivityViewModel.loadVideosForCategory(categoryId)
    }

    if (showDeleteDialog && summaryToDelete != null) {
        ModernDeleteAlert(
            onDismiss = { showDeleteDialog = false; summaryToDelete = null },
            onConfirm = {
                summaryToDelete?.let {
                    mainActivityViewModel.deleteSummaryText(it.id)
                }
                showDeleteDialog = false
                summaryToDelete = null
                mainActivityViewModel.loadVideosForCategory(categoryId)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(24.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(PrimaryOrange, PrimaryOrangeDark)
                                    )
                                )
                        )
                        Text(
                            categoryName,
                            style = MaterialTheme.typography.titleLarge,
                            color = TextBlack,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 4.dp)) {
                        ModernBackButton(onClick = { navController.popBackStack() })
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight
                )
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        if (videoList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        PrimaryOrange.copy(alpha = 0.1f),
                                        Color.Transparent
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = null,
                            tint = TextGray.copy(alpha = 0.5f),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    
                    Text(
                        "Bu kategoride henüz video yok",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextGray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(videoList) { video ->
                    EnhancedVideoItemRow(
                        video = video,
                        onClick = {
                            navController.navigate("summaryDetailScreen/${video.id}")
                        },
                        onLongClick = {
                            summaryToDelete = video
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EnhancedVideoItemRow(video: VideoUiModel, onClick: () -> Unit, onLongClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "videoCardScale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = if (isPressed) 2.dp else 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                interactionSource = interactionSource,
                indication = null
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color.LightGray.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp, 90.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    AsyncImage(
                        model = video.thumbnailUrl,
                        contentDescription = video.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(PrimaryOrange)
                        )
                        Text(
                            text = video.summarySnippet,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernDeleteAlert(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(ErrorRed)
                )
                Text(
                    "Özeti Sil",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextBlack,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Text(
                "Bu özeti silmek istediğinizden emin misiniz?",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray,
                lineHeight = 22.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.shadow(4.dp, RoundedCornerShape(14.dp), spotColor = ErrorRed.copy(alpha = 0.5f))
            ) {
                Text("Sil", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
            ) {
                Text("İptal", color = TextGray)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModernVideoItemRow(video: VideoUiModel, onClick: () -> Unit, onLongClick: () -> Unit) {
    EnhancedVideoItemRow(video = video, onClick = onClick, onLongClick = onLongClick)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoItemRow(video: VideoUiModel, onClick: () -> Unit, onLongClick: () -> Unit) {
    EnhancedVideoItemRow(video = video, onClick = onClick, onLongClick = onLongClick)
}
