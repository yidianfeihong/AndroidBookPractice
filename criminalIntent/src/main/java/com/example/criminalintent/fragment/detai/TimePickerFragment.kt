package com.example.criminalintent.fragment.detai

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment

class TimePickerFragment : DialogFragment() {

    var timeChangeObserver: TimePicker.OnTimeChangedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return TimePicker(requireContext()).apply {
            if (timeChangeObserver != null) {
                setOnTimeChangedListener { view, hourOfDay, minute ->
                    Log.d(TAG, "time change：hourOfDay = $hourOfDay,minute = $minute")
                    timeChangeObserver?.onTimeChanged(
                        view,
                        hourOfDay,
                        minute
                    )
                }
            }
        }
    }

    companion object {
        private const val TAG = "TimePickerFragment"

        @JvmStatic
        fun newInstance(): TimePickerFragment {
            return TimePickerFragment()
        }
    }
}