package com.example.condo.feature.auth.data

import com.example.condo.feature.auth.domain.AuthRepository
import com.example.condo.core.data.networking.DataError
import com.example.condo.core.data.networking.EmptyDataResult
import com.example.condo.core.data.networking.Result
import com.example.condo.core.data.networking.models.AccessTokenResponse
import com.example.condo.core.domain.AuthInfo
import com.example.condo.core.domain.SessionStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val sessionStorage: SessionStorage
) : AuthRepository {
    override suspend fun login(
        email: String,
        password: String
    ): EmptyDataResult<DataError.Network> =
        withContext(Dispatchers.IO) {

            val result = httpClient.post(
                urlString = "https://api.i-dsolution.com/oauth/token?grant_type=password&scope=read_profile&username=david@ldctechnologie.com&password=icondo"
            )
            if (result.status.isSuccess()) {
                val json =
                    Json {
                        ignoreUnknownKeys = true
                    } // Pour ignorer les clés inconnues si nécessaire
                val accessTokenResponse =
                    json.decodeFromString<AccessTokenResponse>(result.body())
                sessionStorage.set(
                    AuthInfo(
                        accessToken = accessTokenResponse.accessToken,
                        userId = accessTokenResponse.jti
                    )
                )
                Result.Success(Unit)
            } else {
                Result.Error(DataError.Network.SERVER_ERROR)
            }
        }
}