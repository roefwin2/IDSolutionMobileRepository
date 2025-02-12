package com.example.testkmpapp.core.data.di

import com.example.testkmpapp.core.data.auth.EncryptedSessionsStorage
import com.example.testkmpapp.core.data.networking.HttpClientFactory
import com.example.testkmpapp.core.domain.SessionStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    singleOf(::EncryptedSessionsStorage).bind<SessionStorage>()
    single {
        HttpClientFactory(get()).build(get())
    }
}