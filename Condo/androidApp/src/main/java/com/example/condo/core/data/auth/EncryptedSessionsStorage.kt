package com.example.condo.core.data.auth

import android.content.SharedPreferences
import com.example.condo.core.domain.AuthInfo
import com.example.condo.core.domain.SessionStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EncryptedSessionsStorage(
    private val sharedPreferences: SharedPreferences
) : SessionStorage {
    override suspend fun get(): AuthInfo? {
        return withContext(Dispatchers.IO) {
            val accessToken = sharedPreferences.getString(KEY_AUTH_INFO, null)
            if (accessToken == null) {
                null
            } else {
                AuthInfo(accessToken = accessToken, userId = "")
            }
        }
    }

    override suspend fun set(info: AuthInfo?) {
        withContext(Dispatchers.IO) { // Blocking
            if (info == null) {
                sharedPreferences.edit().remove(KEY_AUTH_INFO).apply()
                return@withContext
            }
            sharedPreferences
                .edit()
                .putString(KEY_AUTH_INFO, info.accessToken)
                .commit()
        }
    }

    companion object {
        private const val KEY_AUTH_INFO = "KEY_AUTH_INFO"
    }
}
