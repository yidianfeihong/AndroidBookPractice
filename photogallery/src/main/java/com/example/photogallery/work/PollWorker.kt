package com.example.photogallery.work

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.photogallery.ui.PhotoGalleryMainActivity
import com.example.photogallery.PhotoGalleryApplication.Companion.NOTIFICATION_CHANNEL_ID
import com.example.photogallery.QueryPreferences
import com.example.photogallery.R
import com.example.photogallery.model.PexelsRepository


class PollWorker(val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {
    companion object {
        const val TAG = "PollWorker"
        const val ACTION_SHOW_NOTIFICATION = "com.example.photogallery.SHOW_NOTIFICATION"
        const val PERMISSION_PRIVATE = "com.example.photogallery.PRIVATE"
        const val REQUEST_CODE = "request_code"
        const val NOTIFICATION = "notification"
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
            val intent = PhotoGalleryMainActivity.newIntent(context)
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
            showBackgroundNotification(notification)
        } else {
            Log.i(TAG, "Got an old result: $resultId")
        }
        return Result.success()
    }

    private fun showBackgroundNotification(notification: Notification) {
        val notificationIntent =
            Intent(ACTION_SHOW_NOTIFICATION).setPackage(context.packageName)
        notificationIntent.putExtra(REQUEST_CODE, 0)
        notificationIntent.putExtra(NOTIFICATION, notification)
        context.sendOrderedBroadcast(
            notificationIntent,
            PERMISSION_PRIVATE
        )
    }
}