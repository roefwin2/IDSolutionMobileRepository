package com.example.testkmpapp.core.domain.usecases

import com.example.testkmpapp.core.data.networking.DataError
import com.example.testkmpapp.core.data.networking.EmptyDataResult
import com.example.testkmpapp.feature.auth.domain.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ICondoLoginUseCase(
    private val authRepository: AuthRepository,
) {
    fun invoke(
        email: String,
        password: String,
        voipUsername: String,
        voiPassword: String,
        domain: String,
    ): Flow<EmptyDataResult<DataError.Network>> = flow {
        val result = authRepository.login(email, password)
        if (result is com.example.testkmpapp.core.data.networking.Result.Error) {
            emit(result)
        }
        if (result is com.example.testkmpapp.core.data.networking.Result.Success) {
            emit(result)
        }
    }
}