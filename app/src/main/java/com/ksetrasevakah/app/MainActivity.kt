package com.ksetrasevakah.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ksetrasevakah.app.navigation.NavGraph
import com.ksetrasevakah.designsystem.theme.KsetraTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single activity host for all Compose navigation destinations.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KsetraTheme {
                NavGraph()
            }
        }
    }
}
