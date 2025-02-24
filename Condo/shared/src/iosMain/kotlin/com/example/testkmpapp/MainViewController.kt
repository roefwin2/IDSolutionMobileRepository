package com.example.testkmpapp

import androidx.compose.ui.window.ComposeUIViewController
import com.example.testkmpapp.feature.mainscreen.NavigationRoot

fun MainViewController() = ComposeUIViewController(configure = {
    enforceStrictPlistSanityCheck = false
}) { NavigationRoot(onIncomingCall = {}) }