package com.example.testkmpapp.core.domain.di

import com.example.testkmpapp.core.domain.usecases.ICondoLoginUseCase
import org.koin.dsl.module

val coreDomainModule = module {
    single {
        ICondoLoginUseCase(get())
    }
}