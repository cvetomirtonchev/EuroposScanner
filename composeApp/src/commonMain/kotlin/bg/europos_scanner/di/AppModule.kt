package bg.europos_scanner.di

import bg.europos_scanner.data.remote.ApiService
import bg.europos_scanner.data.repository.AuthRepository
import bg.europos_scanner.data.repository.OrderRepository
import bg.europos_scanner.data.repository.StudentRepository
import bg.europos_scanner.domain.session.SessionManager
import bg.europos_scanner.ui.login.LoginViewModel
import bg.europos_scanner.ui.orders.AllOrdersViewModel
import bg.europos_scanner.ui.scanner.ScannerViewModel
import bg.europos_scanner.ui.students.StudentsViewModel
import com.russhwolf.settings.Settings
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<Settings> { Settings() }
    singleOf(::SessionManager)
    singleOf(::ApiService)
    singleOf(::AuthRepository)
    singleOf(::StudentRepository)
    singleOf(::OrderRepository)
    viewModelOf(::LoginViewModel)
    viewModelOf(::ScannerViewModel)
    viewModelOf(::StudentsViewModel)
    viewModelOf(::AllOrdersViewModel)
}
