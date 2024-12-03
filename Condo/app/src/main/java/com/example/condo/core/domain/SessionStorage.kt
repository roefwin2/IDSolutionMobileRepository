package com.example.condo.core.domain

interface SessionStorage {

    suspend fun get() : AuthInfo?
    suspend fun set(info: AuthInfo?)
}