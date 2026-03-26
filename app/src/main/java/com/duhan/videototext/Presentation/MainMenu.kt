package com.duhan.videototext.Presentation

import Settings
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.duhan.videototext.Presentation.SelectedCategoryScreen.CategoryScreen
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.ui.revenuecatui.Paywall
import com.revenuecat.purchases.ui.revenuecatui.PaywallListener
import com.revenuecat.purchases.ui.revenuecatui.PaywallOptions

sealed class BottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    object Home : BottomNavItem("menuScreen", Icons.Default.Home, "Home")
    object Search : BottomNavItem("searchScreen", Icons.Default.Search, "Search")
    object Categories : BottomNavItem("categories_list", Icons.Default.List, "Categories") // Kategori yönlendirmesi için yeni bir rota
    object Settings : BottomNavItem("Settings", Icons.Default.Settings, "Settings")
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainMenu(
    mainActivityViewModel: MainActivityViewModel,
    activity: android.app.Activity
) {
    val navController = rememberNavController()
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showPaywall by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val mainScreenRoutes = listOf(
        BottomNavItem.Home.route,
        BottomNavItem.Search.route,
        BottomNavItem.Categories.route,
        BottomNavItem.Settings.route
    )

    // Alt navigasyon barı için öğe listesi
    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Categories,
        BottomNavItem.Settings
    )

    if (showAddCategoryDialog) {
        AddCategoryDialog(
            onDismiss = { showAddCategoryDialog = false },
            mainActivityViewModel = mainActivityViewModel
        )
    }

    if (showPaywall) {
        Dialog(onDismissRequest = { showPaywall = false }) {
            Paywall(
                options = PaywallOptions.Builder(
                    dismissRequest = { showPaywall = false }
                )
                    .setListener(object : PaywallListener {
                        override fun onPurchaseCompleted(customerInfo: CustomerInfo, storeTransaction: StoreTransaction) {
                            showPaywall = false // Ekranı kapat

                            mainActivityViewModel.checkUserStatus()
                        }

                        override fun onRestoreCompleted(customerInfo: CustomerInfo) {
                            mainActivityViewModel.checkUserStatus()
                        }
                    })
                    .build()
            )
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            if (currentRoute == "menuScreen" || currentRoute == "categories_list") {
                androidx.compose.material3.FloatingActionButton(
                    onClick = { showAddCategoryDialog = true },
                    containerColor = com.duhan.videototext.ui.theme.PrimaryOrange,
                    contentColor = Color.White,
                    elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 6.dp
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Category")
                }
            }
        },
        bottomBar = {
            if (currentRoute in mainScreenRoutes){
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 10.dp
                ) {

                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = com.duhan.videototext.ui.theme.PrimaryOrange,
                                unselectedIconColor = com.duhan.videototext.ui.theme.TextGrayLight,
                                selectedTextColor = com.duhan.videototext.ui.theme.PrimaryOrange,
                                unselectedTextColor = com.duhan.videototext.ui.theme.TextGrayLight,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }

            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "menuScreen",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("menuScreen") {
                MainScreen(
                    navController = navController,
                    mainActivityViewModel = mainActivityViewModel,
                    onAddCategoryClick = { showAddCategoryDialog = true },
                    onSettingsClick = {
                        navController.navigate("Settings") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable("searchScreen") {
                SearchScreen(
                    navController = navController,
                    mainActivityViewModel = mainActivityViewModel,
                    modifier = Modifier.padding(innerPadding),
                    onShowPaywall = { showPaywall = true }
                )
            }
            composable("categories_list") {
                CategoriesScreen(
                    navController = navController,
                    mainActivityViewModel = mainActivityViewModel,
                    modifier = Modifier.padding(innerPadding)
                )
            }

            composable("categories_overview") {
                MainScreen(
                    navController = navController,
                    mainActivityViewModel = mainActivityViewModel,
                    onAddCategoryClick = { showAddCategoryDialog = true },
                    onSettingsClick = { navController.navigate("Settings") }
                )
            }
            composable("Settings") {
                Settings(mainActivityViewModel = mainActivityViewModel)
            }
            composable(
                route = "summaryDetailScreen/{summaryId}",
                arguments = listOf(navArgument("summaryId") { type = NavType.IntType })
            ) { backStackEntry ->
                val summaryId = backStackEntry.arguments?.getInt("summaryId") ?: 0
                SummaryDetailScreen(
                    summaryId = summaryId,
                    mainActivityViewModel = mainActivityViewModel,
                    navController = navController
                )
            }
            composable(
                route = "category/{categoryId}/{categoryName}",
                arguments = listOf(
                    navArgument("categoryId") { type = NavType.IntType },
                    navArgument("categoryName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: 0
                val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Kategori"

                CategoryScreen(
                    categoryId = categoryId,
                    categoryName = categoryName,
                    mainActivityViewModel = mainActivityViewModel,
                    navController = navController
                )
            }
        }
    }
}