package com.example.condo.application

import android.app.Application
import com.example.condo.feature.auth.data.di.authDataModule
import com.example.condo.feature.auth.presentation.di.authViewModelModule
import com.example.condo.core.data.di.coreDataModule
import com.example.condo.di.appModule
import com.example.condo.feature.ssh.data.di.sshDataModule
import com.example.condo.feature.ssh.domain.di.sshDomainModule
import com.example.condo.feature.ssh.presenter.di.sshViewModelModule
import com.example.condo.feature.video.presenter.di.videoViewModelModule
import com.example.voip.voip.data.di.voipDataModule
import com.example.voip.voip.presenter.di.voipViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class CondoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        startKoin {
            androidLogger()
            androidContext(this@CondoApplication)
            modules(
                appModule,
                authDataModule,
                coreDataModule,
                authViewModelModule,
                sshDataModule,
                sshDomainModule,
                sshViewModelModule,
                voipViewModelModule,
                voipDataModule,
                videoViewModelModule,
            )
        }
    }
}