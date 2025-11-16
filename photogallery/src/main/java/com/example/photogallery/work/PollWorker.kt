package com.example.photogallery.work

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.photogallery.PhotoGalleryActivity
import com.example.photogallery.PhotoGalleryApplication.Companion.NOTIFICATION_CHANNEL_ID
import com.example.photogallery.QueryPreferences
import com.example.photogallery.R
import com.example.photogallery.model.PexelsRepository

class PollWorker(val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {
    companion object {
        const val TAG = "PollWorker"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "Work request triggered")
        val query = QueryPreferences.getStoredQuery(context)
        val lastResultId = QueryPreferences.getLastResultId(context)
        val result = if (query.isEmpty()) {
            PexelsRepository().fetchPhotos()
        } else {
            PexelsRepository().searchPhotos(query)
        }
        val items = result.photos
        if (items.isEmpty()) {
            return Result.success()
        }
        val resultId = items.first().id.toString()
        if (lastResultId.isNotEmpty()
            && lastResultId != resultId
        ) {
            Log.i(TAG, "Got a new result: $resultId")
            QueryPreferences.setLastResultId(context, resultId)
            val intent = PhotoGalleryActivity.newIntent(context)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            val resources = context.resources
            val notification = NotificationCompat
                .Builder(context, NOTIFICATION_CHANNEL_ID)
                .setTicker(resources.getString(R.string.new_pictures_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(resources.getString(R.string.new_pictures_title))
                .setContentText(resources.getString(R.string.new_pictures_text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
            val notificationManager = NotificationManagerCompat.from(context)
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notificationManager.notify(0, notification)
            } else {
                Log.e(TAG, "没有通知权限")
            }
        } else {
            Log.i(TAG, "Got an old result: $resultId")
        }
        return Result.success()
    }
}