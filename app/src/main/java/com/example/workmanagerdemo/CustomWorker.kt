package com.example.workmanagerdemo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import okhttp3.internal.notify
import javax.inject.Inject

@HiltWorker
class CustomWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted val api: DemoApi,
    @Assisted workerParams: WorkerParameters
): CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            setForeground(getForegroundInfo(applicationContext))
            val response = api.getPost()
            if (response.isSuccessful) {
                Log.d("CustomWorker","Success ${response.body()?.id} ${response.body()?.title}")
                Result.success()
            } else {
                Log.d("CustomWorker", "Retrying")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.d("CustomWorker", "Error!!")
            Result.failure(Data.Builder().putString("error",e.toString()).build())
        }
        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return getForegroundInfo(applicationContext)
    }
}

private fun getForegroundInfo(context: Context): ForegroundInfo{
    return ForegroundInfo(
        1,
        createNotification(context),
        ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE
    )
}

private fun createNotification(context: Context): Notification {

    val channelId = "main_channel_id"
    val channelName = "main channel"
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle("Notification title")
        .setContentText("This is my first text")
        .setOngoing(true)
//        .setAutoCancel(true)

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         val channel =NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
         notificationManager.createNotificationChannel(channel)
    }

    return  builder.build()

}