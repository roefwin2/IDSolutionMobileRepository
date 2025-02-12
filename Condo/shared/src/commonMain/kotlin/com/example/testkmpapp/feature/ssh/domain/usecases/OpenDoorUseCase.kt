package com.example.testkmpapp.feature.ssh.domain.usecases

import com.example.testkmpapp.core.data.networking.DataError
import com.example.testkmpapp.core.data.networking.Result
import com.example.testkmpapp.feature.ssh.domain.CondoSSHRepository
import com.example.testkmpapp.feature.ssh.domain.models.CondoSite

class OpenDoorUseCase(
    private val condoSSHRepository: CondoSSHRepository
) {

    suspend fun invoke(
        condoSite: CondoSite,
        door: Int
    ): com.example.testkmpapp.core.data.networking.Result<Unit, com.example.testkmpapp.core.data.networking.Error> {
        val startTunnel = condoSSHRepository.startTunnel(
            hostname = condoSite.host,
            localPort = condoSite.port,
            username = "root",
            password = "icondo",
            siteName = condoSite.siteName
        )
        if (startTunnel is Error) return com.example.testkmpapp.core.data.networking.Result.Error(
            DataError.Network.SERVER_ERROR
        )
        val submitLogin = condoSSHRepository.submitLogin(
            username = "admin",
            password = "1234",
            siteName = condoSite.siteName
        )
        if (submitLogin is Error) return com.example.testkmpapp.core.data.networking.Result.Error(
            DataError.Network.SERVER_ERROR
        )
        val result =
            condoSSHRepository.unlockDoor(lobbyDoor = door, siteName = condoSite.siteName)
        return if (result is com.example.testkmpapp.core.data.networking.Result.Success) {
            com.example.testkmpapp.core.data.networking.Result.Success(Unit)
        } else {
            Result.Error(DataError.Network.SERVER_ERROR)
        }
    }
}