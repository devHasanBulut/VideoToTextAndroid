package com.duhan.videototext.Presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.duhan.videototext.Domain.Category
import com.duhan.videototext.Presentation.SelectedCategoryScreen.ModernDeleteAlert
import com.duhan.videototext.ui.theme.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CategoriesScreen(
    navController: NavController,
    mainActivityViewModel: MainActivityViewModel,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    val categoryList by mainActivityViewModel.categoryList.collectAsState()
    val categoryCounts by mainActivityViewModel.categoryCounts.collectAsState()

    LaunchedEffect(Unit) {
        mainActivityViewModel.refreshCategories()
    }

    if (showDeleteDialog && selectedCategory != null) {
        ModernDeleteAlert(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                selectedCategory?.let {
                    mainActivityViewModel.deleteCategory(it.categoryName)
                }
                showDeleteDialog = false
                selectedCategory = null
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Kategoriler",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )
                    )
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
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(
                items = categoryList,
                key = { _, category -> category.id }
            ) { index, category ->
                val count = categoryCounts[category.id] ?: 0
                val isSpecialCategory = category.id == MainActivityViewModel.FavoritesCategoryId ||
                                        category.id == MainActivityViewModel.DownloadsCategoryId

                CategoryRowItem(
                    category = category,
                    index = index,
                    isSpecial = isSpecialCategory,
                    onClick = {
                        val encodedCategoryName = URLEncoder.encode(category.categoryName, StandardCharsets.UTF_8.toString())
                        navController.navigate("category/${category.id}/$encodedCategoryName")
                    },
                    onLongClick = {
                        if (!isSpecialCategory) {
                            selectedCategory = category
                            showDeleteDialog = true
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryRowItem(
    category: Category,
    index: Int,
    isSpecial: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val iconBackgrounds = listOf(
        Color(0xFFE8EAF6),
        Color(0xFFFFEBEE),
        Color(0xFFE3F2FD),
        Color(0xFFFFF3E0),
        Color(0xFFF3E5F5),
        Color(0xFFE0F2F1)
    )
    
    val iconTints = listOf(
        Color(0xFF3F51B5),
        Color(0xFFE91E63),
        Color(0xFF2196F3),
        Color(0xFFFF9800),
        Color(0xFF9C27B0),
        Color(0xFF009688)
    )

    val colorIndex = index % iconBackgrounds.size
    val backgroundColor = iconBackgrounds[colorIndex]
    val iconColor = iconTints[colorIndex]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = category.categoryName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = TextGrayLight,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
