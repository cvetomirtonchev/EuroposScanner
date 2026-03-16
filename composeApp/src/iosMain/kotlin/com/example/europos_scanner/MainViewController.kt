package com.example.europos_scanner

import androidx.compose.ui.window.ComposeUIViewController
import com.example.europos_scanner.di.appModule
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {
    App()
}

fun initKoin() {
    startKoin {
        modules(appModule)
    }
}
