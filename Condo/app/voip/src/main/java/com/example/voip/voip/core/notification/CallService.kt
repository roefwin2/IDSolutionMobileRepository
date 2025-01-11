package com.example.voip.voip.core.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import androidx.annotation.MainThread
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.voip.R
import com.example.voip.voip.domain.ICondoVoip
import org.koin.android.ext.android.inject
import org.linphone.core.tools.Log
import org.linphone.core.tools.service.CoreService

class CallService() : CoreService() {
    private val TAG = "CallService"
    private val notificationManager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(this)
    }

    private val iCondoVoip : ICondoVoip by inject()
    private val channelId = "high_priority_channel"
    private val channelName = "High Priority Notifications"

    companion object {
        const val ACTION_START_CALL_SERVICE = "com.example.condo.ACTION_START_CALL_SERVICE"
        const val ACTION_ANSWER_CALL = "action_answer_call"
        const val ACTION_DECLINE_CALL = "action_decline_call"
        const val KEEP_ALIVE_FOR_THIRD_PARTY_ACCOUNTS_ID = 5
    }

    override fun onCreate() {
        super.onCreate()
        Log.i("$TAG Created")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_CALL_SERVICE -> {
                startKeepAliveServiceForeground()
            }

            ACTION_ANSWER_CALL -> {
                val phoneNumber = intent.getStringExtra("phone_number")
                handleAnswerCall(phoneNumber)
            }

            ACTION_DECLINE_CALL -> {
                val phoneNumber = intent.getStringExtra("phone_number")
                handleDeclineCall(phoneNumber)
            }
        }
        startKeepAliveServiceForeground()
        return START_STICKY
    }

    private fun handleAnswerCall(phoneNumber: String?) {
        phoneNumber?.let {
            // Implémentation de la réponse à l'appel
            // Ceci dépendra de votre implémentation spécifique pour gérer les appels
            // Par exemple:
            // telephonyManager.answerRingingCall()
            // ou votre propre logique de gestion d'appel
            iCondoVoip.answerCall()
        }
        // Arrêter la notification une fois l'appel pris
        stopForeground(true)
    }

    private fun handleDeclineCall(phoneNumber: String?) {
        phoneNumber?.let {
            // Implémentation pour décliner l'appel
            // Par exemple:
            // telephonyManager.endCall()
            // ou votre propre logique de rejet d'appel

            // Vous pourriez aussi vouloir envoyer un SMS automatique
            // sendAutomaticSMS(phoneNumber)
            iCondoVoip.hangUp()
        }

        // Arrêter la notification une fois l'appel rejeté
        stopForeground(true)
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

    override fun createServiceNotificationChannel() {

// Créez le canal de notification pour Android 8.0+ (API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        } else {
            notificationManager.getNotificationChannel(channelId)
        }
    }

    override fun showForegroundServiceNotification(isVideoCall: Boolean) {
        // Intent pour répondre à l'appel
        val answerIntent = Intent(this, CallService::class.java).apply {
            action = ACTION_ANSWER_CALL
            putExtra("phone_number", "+33612345678")
        }

        val answerPendingIntent = PendingIntent.getService(
            this,
            0,
            answerIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent pour décliner l'appel
        val declineIntent = Intent(this, CallService::class.java).apply {
            action = ACTION_DECLINE_CALL
            putExtra("phone_number", "+33612345678")
        }

        val declinePendingIntent = PendingIntent.getService(
            this,
            1,  // Request code différent de celui pour répondre
            declineIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = createIncomingCallNotification(
            context = this,
            channelId = channelId,
            callerName = "John Doe",
            phoneNumber = "+33612345678",
            acceptCallIntent = answerPendingIntent,
            rejectCallIntent = declinePendingIntent
        )

        // Affichez la notification
        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(2, notification)
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
        // Créez un canal de notification (Android 8.0+)
        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                channelId,
                "Foreground Service Channel", // Nom visible par l'utilisateur
                NotificationManager.IMPORTANCE_HIGH // Importance minimale pour éviter les alertes
            )
        } else {
            notificationManager.getNotificationChannel(channelId)
        }
        val notificationManager = getSystemService(NotificationManager::class.java)
        if (channel != null) {
            notificationManager.createNotificationChannel(channel)
        }
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

        // Create channel + notification => keep alive the service
        // Créer le PendingIntent
        val pendingIntent = PendingIntent.getActivity(
            this,
            KEEP_ALIVE_FOR_THIRD_PARTY_ACCOUNTS_ID, // Request code unique
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Définir des flags
        )

        val notification = createIncomingCallNotification(
            context = this,
            channelId = "condo_channel_id",
            callerName = "John Doe",
            phoneNumber = "+33612345678",
            acceptCallIntent = pendingIntent,
            rejectCallIntent = pendingIntent
        )
        Log.i(
            "$TAG Keep alive for third party accounts Service found, starting it as foreground using notification ID [$KEEP_ALIVE_FOR_THIRD_PARTY_ACCOUNTS_ID] with type [SPECIAL_USE]"
        )
        startForeground(
            KEEP_ALIVE_FOR_THIRD_PARTY_ACCOUNTS_ID,
            notification,
        )
    }

    private fun createIncomingCallNotification(
        context: Context,
        channelId: String,
        callerName: String,
        phoneNumber: String,
        acceptCallIntent: PendingIntent,
        rejectCallIntent: PendingIntent
    ): Notification {

        // Création des actions pour répondre/rejeter l'appel
        val acceptAction = NotificationCompat.Action.Builder(
            R.drawable.ic_call_accept,
            context.getString(R.string.accept_call),
            acceptCallIntent
        ).build()

        val rejectAction = NotificationCompat.Action.Builder(
            R.drawable.ic_call_reject,
            context.getString(R.string.reject_call),
            rejectCallIntent
        ).build()

        // Configuration de la notification d'appel entrant
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_call_notification)
            .setContentTitle(callerName)
            .setContentText(phoneNumber)
            //.setLargeIcon(getCallerAvatar(context, phoneNumber)) // Méthode à implémenter pour récupérer l'avatar
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Priorité élevée
            .setAutoCancel(true)
            .setOngoing(true)
            .addAction(acceptAction)
            .addAction(rejectAction)
            .setTimeoutAfter(60000) // Timeout après 1 minute
            .build()
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