package com.example.condo.feature.auth.data.di

import com.example.condo.feature.auth.data.AuthRepositoryImpl
import com.example.condo.feature.auth.domain.AuthRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authDataModule = module {
    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
}