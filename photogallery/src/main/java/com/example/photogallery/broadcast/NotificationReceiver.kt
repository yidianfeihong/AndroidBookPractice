package com.example.photogallery.broadcast

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.photogallery.work.PollWorker

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "NotificationReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive action = ${intent?.action}")
        if (resultCode != Activity.RESULT_OK || intent == null) {
            return
        }
        val requestCode: Int = intent.getIntExtra(PollWorker.REQUEST_CODE, 0)
        val notification: Notification? =
            intent.getParcelableExtra(PollWorker.NOTIFICATION)
        if (context != null && notification != null) {
            val notificationManager = NotificationManagerCompat.from(context)
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notificationManager.notify(requestCode, notification)
            } else {
                Log.e(PollWorker.TAG, "没有通知权限")
            }
        }
    }
}