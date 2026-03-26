package com.duhan.videototext.Presentation

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.duhan.videototext.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    mainActivityViewModel: MainActivityViewModel,
    modifier: Modifier = Modifier,
    onShowPaywall: () -> Unit = {}
) {
    val uiState by mainActivityViewModel.searchUiState.collectAsState()
    val searchHistory = mainActivityViewModel.searchHistoryList
    val searchText by mainActivityViewModel.searchText.collectAsState()
    val context = LocalContext.current
    
    val isPremium by mainActivityViewModel.isPremiumUser.collectAsState()
    val remainingQuota by mainActivityViewModel.remainingQuota.collectAsState()
    
    var showQuotaExceededDialog by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            mainActivityViewModel.onSearchedTextChanged("")
        }
    }
    
    LaunchedEffect(uiState) {
        when(val state = uiState) {
            is SearchUiState.Success -> {
                navController.navigate("summaryDetailScreen/${state.summary.id}")
                mainActivityViewModel.resetSearchState()
            }
            is SearchUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                mainActivityViewModel.resetSearchState()
            }
            is SearchUiState.QuotaExceeded -> {
                showQuotaExceededDialog = true
                mainActivityViewModel.resetSearchState()
            }
            else -> {}
        }
    }
    
    if (showQuotaExceededDialog) {
        AlertDialog(
            onDismissRequest = { showQuotaExceededDialog = false },
            title = { Text("Özet Hakkınız Bitti") },
            text = { Text("Ücretsiz kullanıcılar için günlük limit doldu. Sınırsız özet için Premium'a geçiniz") },
            confirmButton = {
                Button(onClick = {
                    showQuotaExceededDialog = false
                    onShowPaywall()
                }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)) {
                    Text("Premium'a Geç")
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuotaExceededDialog = false }) {
                    Text("Kapat", color = TextGray)
                }
            },
            containerColor = Color.White
        )
    }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "YouTube'dan Özet Çıkar", 
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                        color = TextBlack,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    ModernBackButton(onClick = { navController.popBackStack() })
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            if (!isPremium) {
                QuotaIndicator(
                    remainingQuota = remainingQuota,
                    onUpgradeClick = onShowPaywall
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            OutlinedTextField(
                value = searchText,
                onValueChange = { mainActivityViewModel.onSearchedTextChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(16.dp)),
                placeholder = { 
                    Text(
                        "YouTube URL'sini yapıştırın...", 
                        color = TextGrayLight,
                        style = MaterialTheme.typography.bodyLarge
                    ) 
                },
                leadingIcon = {
                    Icon(Icons.Default.Link, contentDescription = null, tint = TextGray)
                },
                trailingIcon = {
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = PrimaryOrange.copy(alpha = 0.5f),
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = PrimaryOrange
                )
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            if (uiState is SearchUiState.Loading) {
                CircularProgressIndicator(color = PrimaryOrange)
            } else {
                Button(
                    onClick = {
                        val url = searchText
                        if (url.isNotBlank()) {
                            mainActivityViewModel.addNewSearchHistory(url)
                            mainActivityViewModel.searchForUrl(url)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(4.dp, RoundedCornerShape(28.dp), spotColor = PrimaryOrange.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    enabled = searchText.isNotBlank(),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(OrangeGradientStart, OrangeGradientEnd)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                            Text(
                                "Özet Oluştur", 
                                color = Color.White, 
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.History, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Arama Geçmişi",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextBlack,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (searchHistory.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(BackgroundLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SearchOff, contentDescription = null, tint = TextGrayLight, modifier = Modifier.size(40.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Henüz arama geçmişiniz yok", color = TextGray)
                        Text(
                            "Özetlediğiniz videolar burada\nlistelenecektir.",
                            color = TextGrayLight,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(searchHistory) { historyItem ->
                        SwipeableHistoryItem(
                            historyItem = historyItem,
                            onItemClick = {
                                mainActivityViewModel.onSearchedTextChanged(it.searchQuery)
                                mainActivityViewModel.searchForUrl(it.searchQuery)
                            },
                            onYoutubeIconClick = { url ->
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                            onDeleteClick = { item ->
                                mainActivityViewModel.deleteSearchHistory(item.searchQuery)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuotaIndicator(
    remainingQuota: Int,
    onUpgradeClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$remainingQuota",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = if(remainingQuota == 0) ErrorRed else PrimaryOrange
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Kalan Özet Hakkı",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGray
                    )
                }
                if (remainingQuota == 0) {
                    Text(
                        text = "Özet hakkınız bitti",
                        style = MaterialTheme.typography.bodySmall,
                        color = ErrorRed
                    )
                }
            }
            
            if (remainingQuota == 0) {
                Button(
                    onClick = onUpgradeClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFfdc830), Color(0xFFf37335)) // Orange-ish gradient
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "Premium'a Geç",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }
                }
            }
        }
    }
}


