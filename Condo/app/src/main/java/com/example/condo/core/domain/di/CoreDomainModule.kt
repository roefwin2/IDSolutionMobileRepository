package com.example.condo.core.domain.di

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.condo.core.domain.usecases.ICondoLoginUseCase
import com.example.condo.feature.auth.domain.AuthRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDomainModule = module {
    single {
        ICondoLoginUseCase(get(),get())
    }
}