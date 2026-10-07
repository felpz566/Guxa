package com.felpz.guxa.notifications

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.felpz.guxa.MainActivity
import com.felpz.guxa.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class GuxaMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        NotificationChannels.create(this)

        val title = message.notification?.title ?: message.data["title"] ?: "Guxa"
        val body = message.notification?.body ?: message.data["body"] ?: "Nova atividade"
        val channel = message.data["channel"] ?: NotificationChannels.MESSAGES

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("notification_target", message.data["target"])
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            message.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        NotificationManagerCompat.from(this).notify(
            message.hashCode(),
            NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()
        )
    }
}
