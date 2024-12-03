package com.example.condo.core.data.networking

import com.example.condo.core.data.networking.models.AccessTokenResponse
import com.example.condo.core.domain.AuthInfo
import com.example.condo.core.domain.SessionStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import timber.log.Timber

class HttpClientFactory(
    private val sessionStorage: SessionStorage
) {

    fun build(): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) { // everything parsong data and converter what type of json library you want to use
                json(
                    json = Json {
                        ignoreUnknownKeys = true // JUST IGNORE WHEN PARSING DATA NOT GOOD
                    }
                )
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            Timber.d(message)
                        }
                    }
                    level = LogLevel.ALL
                }
                defaultRequest { // Interceptor
                    contentType(ContentType.Application.Json) // Request expect Json input/output
                }
            }
            install(Auth) {
                basic {
                    username = "icondo_web"
                    password = "123456"
                }
                bearer {
                    loadTokens {
                        val accessToken = sessionStorage.get()?.accessToken ?: ""
                        BearerTokens(accessToken = accessToken, refreshToken = "")
                    }
                    refreshTokens {
                        val response = client.post(
                            urlString = "https://api.i-dsolution.com/oauth/token?grant_type=password&scope=read_profile&username=david@ldctechnologie.com&password=icondo",
                            block = {
                                request {
                                    header("Authorization", "Basic aWNvbmRvX3dlYjoxMjM0NTY=")
                                }
                            }
                        )
                        if (response.status.isSuccess()) {
                            val json =
                                Json {
                                    ignoreUnknownKeys = true
                                }
                            val accessTokenResponse =
                                json.decodeFromString<AccessTokenResponse>(response.body())
                            val newAuthInfo = AuthInfo(
                                accessToken = accessTokenResponse.accessToken,
                                userId = accessTokenResponse.jti
                            )
                            sessionStorage.set(newAuthInfo)
                            BearerTokens(
                                accessToken = accessTokenResponse.accessToken,
                                refreshToken = ""
                            )
                        } else {
                            BearerTokens(accessToken = "", refreshToken = "")
                        }

                    }
                }
            }
        }
    }
}

val CONDO_URL = "https://api.i-dsolution.com"