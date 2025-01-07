package com.example.condo.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.condo.feature.auth.data.di.authDataModule
import com.example.condo.feature.auth.presentation.di.authViewModelModule
import com.example.condo.core.data.di.coreDataModule
import com.example.condo.di.appModule
import com.example.condo.feature.ssh.data.di.sshDataModule
import com.example.condo.feature.ssh.domain.di.sshDomainModule
import com.example.condo.feature.ssh.presenter.di.sshViewModelModule
import com.example.condo.feature.video.presenter.di.videoViewModelModule
import com.example.voip.voip.core.di.coreModule
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "condo_channel_id",
                "Channel name",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
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
                coreModule
            )
        }
    }
}