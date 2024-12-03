package com.example.condo.feature.ssh.domain

import com.example.condo.core.data.networking.DataError
import com.example.condo.core.data.networking.EmptyDataResult
import com.example.condo.core.data.networking.Result
import com.example.condo.feature.ssh.domain.models.CondoSite

interface CondoSSHRepository {
    suspend fun domains() : Result<List<CondoSite>, DataError.Network>
    suspend fun startTunnel(hostname :String,localPort :Int,username:String,password :String,siteName:String): EmptyDataResult<DataError.Network>
    suspend fun submitLogin(username:String,password :String,siteName:String): EmptyDataResult<DataError.Network>
    suspend fun unlockDoor(lobbyDoor : Int,siteName: String): Result<String, DataError.Network>
}