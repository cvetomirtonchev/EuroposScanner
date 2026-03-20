package bg.europos_scanner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import bg.europos_scanner.domain.session.SessionManager
import bg.europos_scanner.navigation.AllOrdersRoute
import bg.europos_scanner.navigation.LoginRoute
import bg.europos_scanner.navigation.ScannerRoute
import bg.europos_scanner.navigation.StudentsRoute
import bg.europos_scanner.ui.login.LoginScreen
import bg.europos_scanner.ui.login.LoginViewModel
import bg.europos_scanner.ui.orders.AllOrdersEffect
import bg.europos_scanner.ui.orders.AllOrdersScreen
import bg.europos_scanner.ui.orders.AllOrdersViewModel
import bg.europos_scanner.ui.scanner.ScannerEffect
import bg.europos_scanner.ui.scanner.ScannerScreen
import bg.europos_scanner.ui.scanner.ScannerViewModel
import bg.europos_scanner.ui.students.StudentsEffect
import bg.europos_scanner.ui.students.StudentsScreen
import bg.europos_scanner.ui.students.StudentsViewModel
import bg.europos_scanner.ui.theme.EuroposScannerTheme
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
                            ScannerEffect.NavigateToAllOrders -> {
                                navController.navigate(AllOrdersRoute)
                            }
                            ScannerEffect.NavigateToAllStudents -> {
                                navController.navigate(StudentsRoute)
                            }
                        }
                    }
                }

                ScannerScreen(viewModel = viewModel)
            }
            composable<StudentsRoute> {
                val viewModel: StudentsViewModel = koinViewModel()

                LaunchedEffect(Unit) {
                    viewModel.effect.collect { effect ->
                        when (effect) {
                            StudentsEffect.NavigateToLogin -> {
                                navController.navigate(LoginRoute) {
                                    popUpTo(ScannerRoute) { inclusive = true }
                                }
                            }
                        }
                    }
                }

                StudentsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable<AllOrdersRoute> {
                val viewModel: AllOrdersViewModel = koinViewModel()

                LaunchedEffect(Unit) {
                    viewModel.effect.collect { effect ->
                        when (effect) {
                            AllOrdersEffect.NavigateToLogin -> {
                                navController.navigate(LoginRoute) {
                                    popUpTo(ScannerRoute) { inclusive = true }
                                }
                            }
                        }
                    }
                }

                AllOrdersScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
