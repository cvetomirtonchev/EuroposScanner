package com.example.europos_scanner

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform