package com.duhan.videototext.Presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.duhan.videototext.Data.LocalDataSource.SummaryTextModel
import com.duhan.videototext.ui.theme.BackgroundLight
import com.duhan.videototext.ui.theme.OrangeGradientEnd
import com.duhan.videototext.ui.theme.OrangeGradientStart
import com.duhan.videototext.ui.theme.PrimaryOrange
import com.duhan.videototext.ui.theme.TextBlack
import com.duhan.videototext.ui.theme.TextGray
import com.duhan.videototext.ui.theme.TextGrayLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    mainActivityViewModel: MainActivityViewModel,
    onAddCategoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryList by mainActivityViewModel.categoryList.collectAsState()
    val categoryCounts by mainActivityViewModel.categoryCounts.collectAsState()
    var isSearchExpanded by remember { mutableStateOf(false) }

    if (isSearchExpanded) {
        FullScreenSearch(
            onDismiss = { isSearchExpanded = false },
            onSearch = { url ->
                isSearchExpanded = false
                mainActivityViewModel.searchForUrl(url) 
                navController.navigate("searchScreen") 
            }
        )
    } else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Özetleyici",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextBlack
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = TextBlack
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                        UrlInputSection(
                            onClick = { isSearchExpanded = true }
                        )
                    }
                }

                item {

                     Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                         ProTipCard()
                     }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Son Özetler",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextBlack
                            )
                        )

                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Recent Summaries List
                    val lastScannedSummaries by mainActivityViewModel.lastScannedSummaries.collectAsState()
                    
                    if (lastScannedSummaries.isEmpty()) {
                        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                            EmptySummariesState(onClick = { isSearchExpanded = true })
                        }
                    } else {
                         LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            itemsIndexed(lastScannedSummaries) { index, summary ->
                                 CompactSummaryCard(summary) {
                                      navController.navigate("summaryDetailScreen/${summary.id}")
                                 }
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Kategoriler",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextBlack
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00C853))
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categoryList) { category ->
                            val encodedCategoryName = java.net.URLEncoder.encode(category.categoryName, java.nio.charset.StandardCharsets.UTF_8.toString())
                            
                            CategoryCard(
                                title = category.categoryName,
                                iconColor = PrimaryOrange,
                                backgroundColor = BackgroundLight,
                                modifier = Modifier.width(148.dp),
                                onClick = {
                                    navController.navigate("category/${category.id}/$encodedCategoryName")
                                }
                            )
                        }
                        
                        item {
                            AddCategoryCard(onClick = onAddCategoryClick)
                        }
                    }
                }
                
                item {
                     Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun UrlInputSection(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.05f))
            .background(Color.White, RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Link,
                contentDescription = null,
                tint = TextGrayLight,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "YouTube URL'nizi girin...",
                style = MaterialTheme.typography.bodyLarge,
                color = TextGrayLight
            )
        }
    }
}

@Composable
fun EmptySummariesState(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(Color.White, RoundedCornerShape(32.dp))
            .border(1.dp, Color(0xFFF0F0F0), RoundedCornerShape(32.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(BackgroundLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = TextGrayLight,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Henüz özet oluşturmadınız",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = TextBlack
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Video URL'si ekleyerek ilk özetinizi\noluşturun",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextGrayLight
                    ),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}



@Composable
fun CompactSummaryCard(summary: SummaryTextModel, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(200.dp)
            .height(140.dp),
         shape = RoundedCornerShape(20.dp),
         colors = CardDefaults.cardColors(containerColor = Color.White),
         elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
         Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
             Text(
                 text = summary.videoTitle ?: "Video Summary", 
                 maxLines = 2, 
                 fontWeight = FontWeight.Bold,
                 color = TextBlack
             )
             Text(
                 text = summary.summaryText, 
                 maxLines = 3, 
                 style = MaterialTheme.typography.bodySmall, 
                 color = TextGray,
                 modifier = Modifier.align(Alignment.Center)
             )
         }
    }
}



@Composable
fun ProTipCard() {
    Card(
        modifier = Modifier.fillMaxWidth().height(80.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)) // Light Orange
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryOrange)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("İpucu", fontWeight = FontWeight.Bold, color = PrimaryOrange)
                Text("Uzun videoları özetlemek zaman alabilir.", style = MaterialTheme.typography.bodySmall, color = TextBlack)
            }
        }
    }
}


