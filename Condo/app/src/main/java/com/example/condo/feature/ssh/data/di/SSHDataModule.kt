package com.example.condo.feature.ssh.data.di

import com.example.condo.feature.ssh.data.CondoSSHRepositoryImpl
import com.example.condo.feature.ssh.domain.CondoSSHRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val sshDataModule = module {
    singleOf(::CondoSSHRepositoryImpl).bind<CondoSSHRepository>()
}