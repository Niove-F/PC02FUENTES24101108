package com.example.pc02fuentes24101108.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pc02fuentes24101108.presentation.auth.LoginScreen
import com.example.pc02fuentes24101108.presentation.home.HomeScreen

@Composable
fun AppNavGraph(){
    val navController = rememberNavController()

    NavHost(navController = navController,
        startDestination = "login"){
        composable("login"){ LoginScreen(navController) }
        composable("home"){ HomeScreen() }
    }
}