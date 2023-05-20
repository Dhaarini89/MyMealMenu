package com.android.example.mymealmenu.ui.notification

import android.app.PendingIntent

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.android.example.mymealmenu.MainActivity
import com.android.example.mymealmenu.Notification_CHANNEL_ID

class MenuNotificationWorker(private val context: Context,val workerParameters: WorkerParameters): CoroutineWorker(context,workerParameters)
{
    val notificationTitle ="Meal Planner"
    val notificationContent ="Hi..View your Meal planning for this week"

    override suspend fun doWork(): Result {
        notifyUser()
        return Result.success()
    }

    private fun notifyUser()
    {
        val intent = MainActivity.newIntent(context)
        val pendingIntent = PendingIntent.getActivity(context,10,intent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat
            .Builder(context, Notification_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_report_image)
            .setContentTitle(notificationTitle)
            .setContentText(notificationContent)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(10,notification)

    }

}