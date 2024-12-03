package com.example.condo.core.data.di

import com.example.condo.core.data.auth.EncryptedSessionsStorage
import com.example.condo.core.data.networking.HttpClientFactory
import com.example.condo.core.domain.SessionStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    singleOf(::EncryptedSessionsStorage).bind<SessionStorage>()
    single {
        HttpClientFactory(get()).build()
    }
}