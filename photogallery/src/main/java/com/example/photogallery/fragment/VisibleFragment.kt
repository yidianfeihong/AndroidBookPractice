package com.example.photogallery.fragment

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.photogallery.work.PollWorker

abstract class VisibleFragment : Fragment() {

    val onShowNotification = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(requireContext(),
                "Got a broadcast: ${intent?.action}",
                Toast.LENGTH_LONG)
                .show()
            resultCode = Activity.RESULT_CANCELED
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(PollWorker.ACTION_SHOW_NOTIFICATION)
        ContextCompat.registerReceiver(
            requireActivity(),
            onShowNotification,
            filter,
            PollWorker.PERMISSION_PRIVATE,
            null,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(onShowNotification)
    }
}