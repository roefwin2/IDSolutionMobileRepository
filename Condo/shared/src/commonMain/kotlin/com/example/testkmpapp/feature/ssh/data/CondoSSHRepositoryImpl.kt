package com.example.testkmpapp.feature.ssh.data

import com.example.testkmpapp.core.data.networking.DataError
import com.example.testkmpapp.core.data.networking.EmptyDataResult
import com.example.testkmpapp.core.data.networking.Result
import com.example.testkmpapp.feature.ssh.data.models.SitesDto
import com.example.testkmpapp.feature.ssh.data.models.StartTunnelRequest
import com.example.testkmpapp.feature.ssh.data.models.SubmitLoginRequest
import com.example.testkmpapp.feature.ssh.data.models.toDomain
import com.example.testkmpapp.feature.ssh.domain.CondoSSHRepository
import com.example.testkmpapp.feature.ssh.domain.models.CondoSite
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class CondoSSHRepositoryImpl(
    private val httpClient: HttpClient
) : CondoSSHRepository {

    override suspend fun domains(): Result<List<CondoSite>, DataError.Network> =
        withContext(Dispatchers.IO) {

            val response = httpClient.get(urlString = "https://api.i-dsolution.com/sites")

            if (response.status.isSuccess()) {
                val json =
                    Json {
                        ignoreUnknownKeys = true
                    } // Pour ignorer les clés inconnues si nécessaire
                val sitesDto =
                    json.decodeFromString<SitesDto>(response.body())
                com.example.testkmpapp.core.data.networking.Result.Success(sitesDto.toDomain())
            } else {
                com.example.testkmpapp.core.data.networking.Result.Error(DataError.Network.SERVER_ERROR)
            }
        }

    override suspend fun startTunnel(
        hostname: String,
        localPort: Int,
        username: String,
        password: String,
        siteName: String
    ): EmptyDataResult<DataError.Network> = withContext(Dispatchers.IO) {
        val response = httpClient.post(
            urlString = "https://api.i-dsolution.com/ssh/start-tunnel"
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                StartTunnelRequest(
                    hostname = hostname,
                    port = localPort,
                    password = password,
                    username = username,
                    siteName = siteName
                )
            )
        }
        if (response.status.isSuccess()) {
            com.example.testkmpapp.core.data.networking.Result.Success(Unit)
        } else {
            com.example.testkmpapp.core.data.networking.Result.Error(DataError.Network.SERVER_ERROR)
        }
    }

    override suspend fun submitLogin(
        username: String,
        password: String,
        siteName: String
    ): EmptyDataResult<DataError.Network> = withContext(Dispatchers.IO) {
        val response = httpClient.post(
            urlString = "https://api.i-dsolution.com/sites/submit_login"
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                SubmitLoginRequest(
                    password = password,
                    username = username,
                    siteName = siteName
                )
            )
        }
        if (response.status.isSuccess()) {
            com.example.testkmpapp.core.data.networking.Result.Success(Unit)
        } else {
            com.example.testkmpapp.core.data.networking.Result.Error(DataError.Network.SERVER_ERROR)
        }
    }

    override suspend fun unlockDoor(
        lobbyDoor: Int,
        siteName: String
    ): com.example.testkmpapp.core.data.networking.Result<String, DataError.Network> =
        withContext(Dispatchers.IO) {
            val response = httpClient.get(
                urlString = "https://api.i-dsolution.com/sites/unlockDoor?lobbyDoor=$lobbyDoor&siteName=$siteName"
            )
            if (response.status.isSuccess()) {
                com.example.testkmpapp.core.data.networking.Result.Success("Success")
            } else {
                com.example.testkmpapp.core.data.networking.Result.Error(DataError.Network.SERVER_ERROR)
            }
        }

    override suspend fun getCamera(siteId: String): com.example.testkmpapp.core.data.networking.Result<String, DataError.Network> =
        withContext(Dispatchers.IO) {

            val response = httpClient.get(urlString = "https://api.i-dsolution.com/camera") {
                setBody(siteId)
            }

            if (response.status.isSuccess()) {
                com.example.testkmpapp.core.data.networking.Result.Success(
                    response.body<String>().toString()
                )
            } else {
                com.example.testkmpapp.core.data.networking.Result.Error(DataError.Network.SERVER_ERROR)
            }
        }
}