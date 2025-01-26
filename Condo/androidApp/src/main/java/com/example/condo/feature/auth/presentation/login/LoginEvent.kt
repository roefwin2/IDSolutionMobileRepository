package com.example.condo.feature.auth.presentation.login

import com.example.condo.core.presentation.helper.UiText

sealed interface LoginEvent {
    data class Error(val error: UiText): LoginEvent
    data object LoginSuccess: LoginEvent
}