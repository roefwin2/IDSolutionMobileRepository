package com.example.condo.feature.auth.presentation.login

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.TextFieldState
import com.example.condo.feature.ssh.data.models.SiteDto

data class LoginState @OptIn(ExperimentalFoundationApi::class) constructor(
    val email: TextFieldState = TextFieldState(),
    val password: TextFieldState = TextFieldState(),
    val isPasswordVisible: Boolean = false,
    val canLogin: Boolean = false,
    val isLoggingIn: Boolean = false,
    val doors : List<SiteDto> = emptyList()
)