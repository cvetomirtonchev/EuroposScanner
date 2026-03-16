package com.example.europos_scanner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.europos_scanner.domain.session.SessionManager
import com.example.europos_scanner.navigation.LoginRoute
import com.example.europos_scanner.navigation.ScannerRoute
import com.example.europos_scanner.ui.login.LoginEffect
import com.example.europos_scanner.ui.login.LoginScreen
import com.example.europos_scanner.ui.login.LoginViewModel
import com.example.europos_scanner.ui.scanner.ScannerEffect
import com.example.europos_scanner.ui.scanner.ScannerScreen
import com.example.europos_scanner.ui.scanner.ScannerViewModel
import com.example.europos_scanner.ui.theme.EuroposScannerTheme
import org.koin.compose.viewmodel.koinViewModel
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun App() {
    EuroposScannerTheme {
        val navController = rememberNavController()
        val sessionManager = getKoin().get<SessionManager>()
        val startDestination: Any = if (sessionManager.isLoggedIn) ScannerRoute else LoginRoute

        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable<LoginRoute> {
                val viewModel: LoginViewModel = koinViewModel()
                LoginScreen(
                    viewModel = viewModel,
                    onNavigateToScanner = {
                        navController.navigate(ScannerRoute) {
                            popUpTo(LoginRoute) { inclusive = true }
                        }
                    }
                )
            }
            composable<ScannerRoute> {
                val viewModel: ScannerViewModel = koinViewModel()

                LaunchedEffect(Unit) {
                    viewModel.effect.collect { effect ->
                        when (effect) {
                            ScannerEffect.NavigateToLogin -> {
                                navController.navigate(LoginRoute) {
                                    popUpTo(ScannerRoute) { inclusive = true }
                                }
                            }
                        }
                    }
                }

                ScannerScreen(viewModel = viewModel)
            }
        }
    }
}
