package com.example.europos_scanner

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.europos_scanner.navigation.LoginRoute
import com.example.europos_scanner.navigation.ScannerRoute
import com.example.europos_scanner.ui.login.LoginScreen
import com.example.europos_scanner.ui.login.LoginViewModel
import com.example.europos_scanner.ui.scanner.ScannerScreen
import com.example.europos_scanner.ui.scanner.ScannerViewModel
import com.example.europos_scanner.ui.theme.EuroposScannerTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    EuroposScannerTheme {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = LoginRoute
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
                ScannerScreen(viewModel = viewModel)
            }
        }
    }
}
