package com.duhan.videototext.Presentation

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.duhan.videototext.R
import com.duhan.videototext.ui.theme.VideoToTextTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("CoroutineCreationDuringComposition", "MissingInflatedId", "ObsoleteSdkInt")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            VideoToTextTheme {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    MainMenu(
                        mainActivityViewModel = mainActivityViewModel,
                        activity = this
                    )
                }
            }
        }

    }
}


