package com.mlkitexample.yogachallenge.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import com.mlkitexample.yogachallenge.R
import com.mlkitexample.yogachallenge.view.MainActivity

class AlarmReceiver : BroadcastReceiver() {
    val NOTIFICATION_ID = 0
    val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        deliverNotification(context)
    }

    fun deliverNotification(context: Context){
        createNotification(context)
        val intent = Intent(context,
            MainActivity::class.java)
        intent.putExtra("getPose",true)
        val contentPendingIntent = PendingIntent.getActivity(context,NOTIFICATION_ID,intent,
            PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(context,PRIMARY_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Yoga Challenge!!")
            .setContentText("Can you do this?")
            .setContentIntent(contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
        notificationManager.notify(NOTIFICATION_ID,builder.build())
    }

    fun createNotification(context: Context){
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(PRIMARY_CHANNEL_ID,"Stand up notification",
                NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Notifies every morning to yoga challenge"
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
