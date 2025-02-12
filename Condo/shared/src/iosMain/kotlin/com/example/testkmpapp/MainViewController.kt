package com.example.testkmpapp

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.example.testkmpapp.core.data.datastore.createDataStore
import com.example.testkmpapp.feature.mainscreen.MainScreen
import com.example.testkmpapp.feature.mainscreen.NavigationRoot

fun MainViewController() = ComposeUIViewController(configure = {
}) { NavigationRoot(onIncomingCall = {}, prefs = remember {
    createDataStore()
}) }