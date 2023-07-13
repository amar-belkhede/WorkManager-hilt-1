package com.example.workmanagerdemo

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Inject

@HiltWorker
class CustomWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted val api: DemoApi,
    @Assisted workerParams: WorkerParameters
): CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return try {
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
}