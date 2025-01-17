package com.example.condo

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.condo.feature.auth.presentation.intro.IntroScreenRoot
import com.example.condo.feature.auth.presentation.login.LoginScreenRoot

@Composable
fun NavigationRoot(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(modifier = modifier, navController = navController, startDestination = "auth") {
        authGGraph(navController)
    }
}

private fun NavGraphBuilder.authGGraph(navController: NavHostController) {
    navigation(
        startDestination = "intro",
        route = "auth"
    ) {
        composable(route = "intro") {
            IntroScreenRoot(
                onSignInClick = {
                    navController.navigate("login")
                },
                onSignUpClick = {}
            )
        }
        composable(route = "login") {
            LoginScreenRoot(
                onSignUpClick = {},
                onLoginSuccess = {
                    navController.navigate("mainscreen")
                }
            )
        }
        composable("mainscreen") {
            MainScreen()
        }
    }
}