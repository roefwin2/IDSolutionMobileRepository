package com.example.condo.feature.auth.presentation.login

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.textAsFlow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condo.feature.auth.domain.AuthRepository
import com.example.condo.core.data.networking.DataError
import com.example.condo.core.data.networking.Result
import com.example.condo.core.domain.usecases.ICondoLoginUseCase
import com.example.condo.core.presentation.helper.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.linphone.core.TransportType

@OptIn(
    ExperimentalFoundationApi::class
)
class LoginViewModel(
    private val iCondoLoginUseCase: ICondoLoginUseCase
) : ViewModel() {

    var state by mutableStateOf(LoginState())
        private set

    private val eventChannel = Channel<LoginEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        combine(state.email.textAsFlow(), state.password.textAsFlow()) { email, password ->
            state = state.copy(
                canLogin = true
            )
        }.launchIn(viewModelScope)
    }

    fun onAction(action: LoginAction) {
        when (action) {
            LoginAction.OnLoginClick -> login()
            LoginAction.OnTogglePasswordVisibility -> {
                state = state.copy(
                    isPasswordVisible = !state.isPasswordVisible
                )
            }

            else -> Unit
        }
    }

    private fun login() {
        viewModelScope.launch {
            state = state.copy(isLoggingIn = true)
            iCondoLoginUseCase.invoke(
                email = state.email.text.toString().trim(),
                password = state.password.text.toString(),
                voipUsername = "regis_test",
                voiPassword = "e1d2o3U4",
                domain = "sip.linphone.org",
                transportType = TransportType.Tls
            ).collectLatest { result ->
                state = state.copy(isLoggingIn = false)
                when (result) {
                    is Result.Error -> {
                        if (result.error == DataError.Network.UNAUTHORIZED) {
                            eventChannel.send(
                                LoginEvent.Error(
                                    UiText.DynamicString("Incorrect")
                                )
                            )
                        } else {
                            eventChannel.send(LoginEvent.Error(UiText.DynamicString("Error")))
                        }
                    }

                    is Result.Success -> {
                        eventChannel.send(LoginEvent.LoginSuccess)
                    }
                }
            }
        }
    }
}