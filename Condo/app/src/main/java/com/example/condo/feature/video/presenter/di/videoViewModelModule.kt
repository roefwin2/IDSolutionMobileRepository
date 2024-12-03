package com.example.condo.feature.video.presenter.di

import com.example.condo.feature.video.presenter.VideoViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val videoViewModelModule = module {
    viewModelOf(::VideoViewModel)
}