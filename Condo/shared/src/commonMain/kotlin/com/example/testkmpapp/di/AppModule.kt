package com.example.testkmpapp.di

import com.example.testkmpapp.core.data.auth.EncryptedSessionsStorage
import org.koin.dsl.module

val appModule = module {
    single {
        EncryptedSessionsStorage(get())
    }
}