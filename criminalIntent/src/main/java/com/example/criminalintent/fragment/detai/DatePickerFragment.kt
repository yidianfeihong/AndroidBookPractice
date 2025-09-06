package com.example.criminalintent.fragment.detai

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar

class DatePickerFragment : DialogFragment() {
    interface DatePickCallbacks {
        fun onDateSelected(date: Date)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = arguments?.getSerializable(ARG_DATE) as Date
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = date
        val dateSetListener: DatePickerDialog.OnDateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val currentSelectedDate = GregorianCalendar(
                    year,
                    month,
                    dayOfMonth
                ).time
                targetFragment?.let { fragment ->
                    (fragment as? DatePickCallbacks)?.onDateSelected(
                        currentSelectedDate
                    )
                }
                parentFragmentManager.setFragmentResult(REQUEST_KEY_DATE, Bundle().apply {
                    putSerializable(ARG_DATE, currentSelectedDate)
                })
            }
        return DatePickerDialog(
            requireContext(),
            dateSetListener,
            calendar.get(Calendar.YEAR),
            Calendar.MONTH, Calendar.DAY_OF_MONTH
        )
    }


    companion object {
        const val ARG_DATE = "date"
        const val REQUEST_KEY_DATE = "dateRequest"

        @JvmStatic
        fun newInstance(date: Date): DatePickerFragment {
            return DatePickerFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_DATE, date)
                }
            }
        }
    }
}