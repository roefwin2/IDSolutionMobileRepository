package com.example.voip.voip.core.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import androidx.annotation.MainThread
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.example.voip.R
import com.example.voip.voip.core.service.CoreKeepAliveThirdPartyAccountsService
import org.linphone.core.Core
import org.linphone.core.tools.Log
import org.linphone.core.tools.service.CoreService

private const val INCOMING_CALL_ID = 1
private const val KEEP_ALIVE_FOR_THIRD_PARTY_ACCOUNTS_ID = 5

class CallService() : CoreService() {
    private val TAG = "CallService"
    private val notificationManager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(this)
    }

    companion object {
        const val ACTION_START_CALL_SERVICE = "com.example.condo.ACTION_START_CALL_SERVICE"
    }

    override fun onCreate() {
        super.onCreate()
        Log.i("$TAG Created")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startKeepAliveServiceForeground()
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.i("$TAG Task removed, doing nothing")

        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        Log.i("$TAG onDestroy")
        // coreContext.notificationsManager.onInCallServiceDestroyed()

        super.onDestroy()
    }

    override fun createServiceNotification() {
        // Do nothing, app's Notifications Manager will do the job
    }

    override fun showForegroundServiceNotification(isVideoCall: Boolean) {
        // Do nothing, app's Notifications Manager will do the job
    }

    override fun hideForegroundServiceNotification() {
        // Do nothing, app's Notifications Manager will do the job
    }

    @SuppressLint("NewApi")
    @MainThread
    private fun startKeepAliveServiceForeground() {
        Log.i(
            "$TAG Trying to start keep alive for third party accounts foreground Service using call notification"
        )

        val channelId = getString(R.string.notification_channel_service_id)
        val channel = notificationManager.getNotificationChannel(channelId)
        val importance = channel?.importance ?: NotificationManagerCompat.IMPORTANCE_NONE
        if (importance == NotificationManagerCompat.IMPORTANCE_NONE) {
            Log.e(
                "$TAG Keep alive for third party accounts Service channel has been disabled, can't start foreground service!"
            )
        }

        val intent = Intent(this, CallService::class.java).apply {
            // Ajouter des données si nécessaire
            action = CallService.ACTION_START_CALL_SERVICE
        }

// Créer le PendingIntent
        val pendingIntent = PendingIntent.getActivity(
            this,
            KEEP_ALIVE_FOR_THIRD_PARTY_ACCOUNTS_ID, // Request code unique
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Définir des flags
        )
        // Créez un canal de notification (Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Foreground Service Channel", // Nom visible par l'utilisateur
                NotificationManager.IMPORTANCE_LOW // Importance minimale pour éviter les alertes
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setAutoCancel(false)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setShowWhen(false)
            .setContentIntent(pendingIntent)
        val notification = builder.build()

        Log.i(
            "$TAG Keep alive for third party accounts Service found, starting it as foreground using notification ID [$KEEP_ALIVE_FOR_THIRD_PARTY_ACCOUNTS_ID] with type [SPECIAL_USE]"
        )
        startForeground(
            KEEP_ALIVE_FOR_THIRD_PARTY_ACCOUNTS_ID,
            notification,
        )
    }

    @MainThread
    private fun stopKeepAliveServiceForeground() {
        Log.i(
            "$TAG Stopping keep alive for third party accounts foreground Service (was using notification ID)"
        )
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    @SuppressLint("NewApi")
    @MainThread
    private fun createIncomingCallNotificationChannel() {
        val id = getString(R.string.notification_channel_incoming_call_id)
        val name = getString(R.string.notification_channel_incoming_call_name)

        val ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setLegacyStreamType(AudioManager.STREAM_RING)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build()

        val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH).apply {
            description = name
            setSound(ringtone, audioAttributes)
        }
        notificationManager.createNotificationChannel(channel)
    }
}