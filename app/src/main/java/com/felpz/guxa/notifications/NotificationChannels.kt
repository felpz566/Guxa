package com.felpz.guxa.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannels {
    const val MENTIONS = "mentions"
    const val CALLS = "calls"
    const val MESSAGES = "messages"
    const val SERVER = "server"

    fun create(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannels(
            listOf(
                NotificationChannel(MENTIONS, "Menções", NotificationManager.IMPORTANCE_HIGH),
                NotificationChannel(CALLS, "Chamadas", NotificationManager.IMPORTANCE_HIGH),
                NotificationChannel(MESSAGES, "Mensagens", NotificationManager.IMPORTANCE_DEFAULT),
                NotificationChannel(SERVER, "Servidores", NotificationManager.IMPORTANCE_DEFAULT)
            )
        )
    }
}
