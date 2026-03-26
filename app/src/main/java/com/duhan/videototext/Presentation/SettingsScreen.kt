import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.duhan.videototext.Presentation.MainActivityViewModel
import com.duhan.videototext.Presentation.SummaryDialog
import com.duhan.videototext.ui.theme.BackgroundLight
import com.duhan.videototext.ui.theme.BlueIcon
import com.duhan.videototext.ui.theme.BlueLight
import com.duhan.videototext.ui.theme.ErrorRed
import com.duhan.videototext.ui.theme.PremiumGold
import com.duhan.videototext.ui.theme.TextBlack
import com.duhan.videototext.ui.theme.TextGray
import com.duhan.videototext.ui.theme.TextGrayLight
import com.revenuecat.purchases.ui.revenuecatui.Paywall
import com.revenuecat.purchases.ui.revenuecatui.PaywallOptions


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Settings(
    mainActivityViewModel: MainActivityViewModel,
    modifier: Modifier = Modifier
) {

    var showSummaryDialog by remember { mutableStateOf(false) }
    var showPaywall by remember { mutableStateOf(false) }

    val isPremiumUser by mainActivityViewModel.isPremiumUser.collectAsState()

    if (showPaywall) {
        Dialog(onDismissRequest = { showPaywall = false }) {
            Paywall(
                options = PaywallOptions.Builder(
                    dismissRequest = { showPaywall = false }
                ).build()
            )
        }
    }
    
    if (showSummaryDialog){
        SummaryDialog(
            initialRatio = mainActivityViewModel.getSummaryRatio(),
            isPremiumUser = isPremiumUser,
            onDismiss = { showSummaryDialog = false },
            onSave = { newRatio ->
                mainActivityViewModel.saveSummaryRatio(newRatio)
            },
            onShowPaywall = {
                showSummaryDialog = false
                showPaywall = true
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Ayarlar",
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ProfileCardSection(isPremiumUser = isPremiumUser)

            SettingsSectionHeader("ÖZETLENDİRME")
            SettingsCardGroup {
                SettingsItem(
                    title = "Özet Ayarları",
                    subtitle = "Özet uzunluğunu ayarlayın",
                    onClick = { showSummaryDialog = true }
                )
                Divider(color = BackgroundLight, thickness = 1.dp)
                SettingsItem(
                    title = "Premium'a Geç",
                    subtitle = "Tüm özelliklere erişin",
                    badge = "PRO",
                    onClick = { showPaywall = true }
                )
            }

            Text(
                text = "Version 1.0.2",
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall.copy(color = TextGrayLight)
            )
        }
    }
}

@Composable
fun ProfileCardSection(isPremiumUser: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if(isPremiumUser) PremiumGold.copy(alpha=0.1f) else BlueLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = if(isPremiumUser) PremiumGold else BlueIcon,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Column {
                Text(
                    text = if(isPremiumUser) "Premium Kullanıcı" else "Standart Kullanıcı",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextBlack)
                )
                Text(
                    text = if(isPremiumUser) "VideoToText Pro" else "Daha fazlası için yükseltin",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextGray)
                )
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(
            color = TextGray,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        ),
        modifier = Modifier.padding(start = 4.dp, top = 8.dp)
    )
}

@Composable
fun SettingsCardGroup(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String? = null,
    value: String? = null,
    badge: String? = null,
    iconVector: ImageVector? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyLarge.copy(color = TextBlack, fontWeight = FontWeight.Medium)
                )
                if (badge != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(PremiumGold, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(badge, style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
                    }
                }
            }
             if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextGray)
                )
            }
        }
        
        if (value != null) {
            Text(value, style = MaterialTheme.typography.bodyMedium.copy(color = TextGray))
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = null,
            tint = TextGrayLight,
            modifier = Modifier.size(14.dp)
        )
    }
}

