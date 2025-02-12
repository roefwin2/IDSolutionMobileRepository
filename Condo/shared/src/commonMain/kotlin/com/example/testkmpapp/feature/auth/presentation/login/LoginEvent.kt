package com.example.testkmpapp.feature.auth.presentation.login

import com.example.testkmpapp.core.presentation.helper.UiText

sealed interface LoginEvent {
    data class Error(val error: UiText): LoginEvent
    data object LoginSuccess: LoginEvent
}