package com.example.criminalintent.fragment.detai

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.criminalintent.database.entity.Crime
import com.example.criminalintent.databinding.FragmentCrimeBinding
import com.example.criminalintent.fragment.detai.DatePickerFragment.Companion.ARG_DATE
import com.example.criminalintent.fragment.detai.DatePickerFragment.Companion.REQUEST_KEY_DATE
import com.example.criminalintent.viewmodel.CrimeDetailViewmodel
import java.util.Date
import java.util.UUID

private const val ARG_CRIME_ID = "crime_id"

class CrimeDetailFragment : Fragment(), DatePickerFragment.DatePickCallbacks {
    private var crimeId: UUID? = null
    private var binding: FragmentCrimeBinding? = null
    private lateinit var crime: Crime

    private val viewmodel by lazy {
        ViewModelProvider(this)[CrimeDetailViewmodel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        crimeId = arguments?.getSerializable(ARG_CRIME_ID) as? UUID
        viewmodel.loadCrime(crimeId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        binding = FragmentCrimeBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
        viewmodel.crimeLiveData.observe(viewLifecycleOwner) { crime ->
            Log.d(TAG, "receive crime data = $crime")
            crime?.let {
                this.crime = crime
                updateUI()
            }
        }
    }

    private fun updateUI() {
        binding?.crimeTitle?.setText(crime.title)
        binding?.crimeDateButton?.text = crime.date.toString()
        binding?.crimeSolved?.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
        binding?.crimeTitle?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.d(TAG, "afterTextChanged:$s")
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                Log.d(TAG, "beforeTextChanged:$s")
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                Log.d(TAG, "onTextChanged:$s")
                crime.title = s.toString()
            }
        })
        binding?.crimeSolved?.setOnCheckedChangeListener { buttonView, isChecked ->
            crime.isSolved = isChecked
        }
        parentFragmentManager.setFragmentResultListener(
            REQUEST_KEY_DATE,
            this
        ) { requestKey, result ->
            val date = result.getSerializable(ARG_DATE) as? Date
            if (date != null) {
                crime.date = date
                updateUI()
            }
        }

        binding?.crimeDateButton?.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeDetailFragment, REQUEST_CODE_DATE)
                show(this@CrimeDetailFragment.parentFragmentManager, DIALOG_DATE)
            }
        }

        binding?.crimeTimeButton?.setOnClickListener {
            TimePickerFragment.newInstance().apply {
                timeChangeObserver = TimePicker.OnTimeChangedListener { view, hourOfDay, minute ->
                    Log.d(
                        TAG,
                        "time change：hourOfDay = $hourOfDay,minute = $minute"
                    )
                }
            }.show(parentFragmentManager, null)
        }
    }

    override fun onStop() {
        super.onStop()
        viewmodel.saveCrime(crime)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        Log.d(TAG, "onDestroyView")
    }

    override fun onDateSelected(date: Date) {
        crime.date = date
        updateUI()
    }

    companion object {
        private const val TAG = "CrimeDetailFragment"
        private const val REQUEST_CODE_DATE = 0
        private const val DIALOG_DATE = "dialogDate"

        @JvmStatic
        fun newInstance(crimeId: UUID) =
            CrimeDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_CRIME_ID, crimeId)
                }
            }
    }
}