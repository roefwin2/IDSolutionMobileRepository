package com.example.condo.feature.ssh.domain.di

import com.example.condo.feature.ssh.domain.usecases.OpenDoorUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sshDomainModule = module {
    singleOf(::OpenDoorUseCase)
}