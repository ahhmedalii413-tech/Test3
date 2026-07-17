package com.example.pakforces

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.pakforces.ui.navigation.PakForcesNavGraph
import com.example.pakforces.ui.theme.ForceTheme
import com.example.pakforces.ui.theme.PakForcesTheme
import com.example.pakforces.ui.viewmodel.AppViewModel
import com.example.pakforces.ui.viewmodel.AppViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // System splash screen — held until Compose is ready.
        val splash = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT),
        )

        AppViewModelFactory.Factory = AppViewModelFactory(application)

        setContent {
            val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.Factory!!)
            val state by appViewModel.state.collectAsState()
            val forceThemeState by appViewModel.forceTheme.collectAsState()

            val isDark = state.darkTheme ?: isSystemInDarkTheme()
            val forceTheme = when (forceThemeState) {
                com.example.pakforces.data.model.Force.ARMY -> ForceTheme.ARMY
                com.example.pakforces.data.model.Force.AIR_FORCE -> ForceTheme.AIR_FORCE
                com.example.pakforces.data.model.Force.NAVY -> ForceTheme.NAVY
            }

            splash.setKeepOnScreenCondition { state.isLoading }

            PakForcesTheme(forceTheme = forceTheme, darkTheme = isDark) {
                val navController = rememberNavController()
                PakForcesNavGraph(navController = navController, appViewModel = appViewModel)
            }
        }
    }
}
