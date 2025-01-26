package com.example.condo.feature.auth.domain

import com.example.condo.core.data.networking.DataError
import com.example.condo.core.data.networking.EmptyDataResult

interface AuthRepository {
    suspend fun login(email: String, password: String): EmptyDataResult<DataError.Network>
}