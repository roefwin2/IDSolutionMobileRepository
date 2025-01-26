package com.example.condo.core.domain.usecases

import com.example.condo.core.data.networking.Error
import com.example.condo.core.data.networking.Result
import com.example.condo.feature.auth.domain.AuthRepository
import com.example.voip.voip.data.AccountState
import com.example.voip.voip.domain.ICondoVoip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.linphone.core.RegistrationState
import org.linphone.core.TransportType

class ICondoLoginUseCase(
    private val authRepository: AuthRepository,
    private val iCondoVoip: ICondoVoip
) {
    fun invoke(
        email: String,
        password: String,
        voipUsername: String,
        voiPassword: String,
        domain: String,
        transportType: TransportType
    ): Flow<Result<AccountState, Error>> = flow {
        val result = authRepository.login(email, password)
        if (result is Result.Error) {
            emit(result)
        }
        if (result is Result.Success) {
            iCondoVoip.login(voipUsername, voiPassword, domain, transportType)
            emit(Result.Success(AccountState("",RegistrationState.None)))
        }
    }
}