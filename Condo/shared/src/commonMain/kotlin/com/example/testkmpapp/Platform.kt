package com.example.testkmpapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform