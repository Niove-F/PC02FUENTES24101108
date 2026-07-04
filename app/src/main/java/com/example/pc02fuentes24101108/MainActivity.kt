package com.example.pc02fuentes24101108

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.pc02fuentes24101108.presentation.auth.LoginScreen
import com.example.pc02fuentes24101108.presentation.navigation.AppNavGraph
import com.example.pc02fuentes24101108.ui.theme.pc02fuentes24101108AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            pc02fuentes24101108AppTheme {
                AppNavGraph()
            }
        }
    }
}