package com.android.example.mymealmenu

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

import com.android.example.mymealmenu.ui.database.MealMenuRepository

const   val Notification_CHANNEL_ID = "MealMenu"
class MealMenuApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MealMenuRepository.initalize(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val name = "MealMenu"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Notification_CHANNEL_ID,name,importance)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}