package com.example.condo.feature.ssh.domain.usecases

import com.example.condo.core.data.networking.DataError
import com.example.condo.core.data.networking.Error
import com.example.condo.core.data.networking.Result
import com.example.condo.feature.ssh.domain.CondoSSHRepository
import com.example.condo.feature.ssh.domain.models.CondoSite

class OpenDoorUseCase(
    private val condoSSHRepository: CondoSSHRepository
) {

    suspend fun invoke(condoSite: CondoSite, door: Int): Result<Unit, Error> {
        val startTunnel = condoSSHRepository.startTunnel(
            hostname = condoSite.host,
            localPort = condoSite.port,
            username = "root",
            password = "icondo",
            siteName = condoSite.siteName
        )
        if (startTunnel is Error) return Result.Error(DataError.Network.SERVER_ERROR)
        val submitLogin = condoSSHRepository.submitLogin(
            username = "admin",
            password = "1234",
            siteName = condoSite.siteName
        )
        if (submitLogin is Error) return Result.Error(DataError.Network.SERVER_ERROR)
        val result =
            condoSSHRepository.unlockDoor(lobbyDoor = door, siteName = condoSite.siteName)
        return if (result is Result.Success) {
            Result.Success(Unit)
        } else {
            Result.Error(DataError.Network.SERVER_ERROR)
        }
    }
}