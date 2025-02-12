package com.example.testkmpapp.feature.ssh.domain

import com.example.testkmpapp.core.data.networking.DataError
import com.example.testkmpapp.core.data.networking.EmptyDataResult
import com.example.testkmpapp.core.data.networking.Result
import com.example.testkmpapp.feature.ssh.domain.models.CondoSite

interface CondoSSHRepository {
    suspend fun domains(): com.example.testkmpapp.core.data.networking.Result<List<CondoSite>, DataError.Network>
    suspend fun startTunnel(
        hostname: String,
        localPort: Int,
        username: String,
        password: String,
        siteName: String
    ): EmptyDataResult<DataError.Network>

    suspend fun submitLogin(
        username: String,
        password: String,
        siteName: String
    ): EmptyDataResult<DataError.Network>

    suspend fun unlockDoor(
        lobbyDoor: Int,
        siteName: String
    ): com.example.testkmpapp.core.data.networking.Result<String, DataError.Network>

    suspend fun getCamera(siteId: String): Result<String, DataError.Network>
}