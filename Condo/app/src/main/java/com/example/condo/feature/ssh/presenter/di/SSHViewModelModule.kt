package com.example.condo.feature.ssh.presenter.di

import com.example.condo.feature.ssh.presenter.sites.CondoSitesViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val sshViewModelModule = module {
    viewModelOf(::CondoSitesViewModel)
}