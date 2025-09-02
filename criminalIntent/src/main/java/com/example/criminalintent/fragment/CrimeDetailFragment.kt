package com.example.criminalintent.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.criminalintent.database.entity.Crime
import com.example.criminalintent.databinding.FragmentCrimeBinding
import com.example.criminalintent.viewmodel.CrimeDetailViewmodel
import java.util.UUID
import kotlin.random.Random

private const val ARG_CRIME_ID = "crime_id"

class CrimeDetailFragment : Fragment() {
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
        binding?.crimeDate?.text = crime.date.toString()
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

    companion object {
        private const val TAG = "CrimeDetailFragment"

        @JvmStatic
        fun newInstance(crimeId: UUID) =
            CrimeDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_CRIME_ID, crimeId)
                }
            }
    }
}