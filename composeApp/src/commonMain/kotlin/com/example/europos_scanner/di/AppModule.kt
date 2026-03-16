package com.example.europos_scanner.di

import com.example.europos_scanner.data.remote.ApiService
import com.example.europos_scanner.data.repository.AuthRepository
import com.example.europos_scanner.data.repository.StudentRepository
import com.example.europos_scanner.domain.session.SessionManager
import com.example.europos_scanner.ui.login.LoginViewModel
import com.example.europos_scanner.ui.scanner.ScannerViewModel
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
    viewModelOf(::LoginViewModel)
    viewModelOf(::ScannerViewModel)
}
