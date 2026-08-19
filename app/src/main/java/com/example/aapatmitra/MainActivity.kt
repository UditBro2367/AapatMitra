package com.example.aapatmitra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.aapatmitra.ui.MainAppScreen
import com.example.aapatmitra.ui.theme.AapatMitraTheme
import com.example.aapatmitra.viewmodel.AapatMitraViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: AapatMitraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AapatMitraTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}
