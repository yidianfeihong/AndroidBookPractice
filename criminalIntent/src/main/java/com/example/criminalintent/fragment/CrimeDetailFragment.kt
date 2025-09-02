package com.example.criminalintent.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.criminalintent.database.Crime
import com.example.criminalintent.databinding.FragmentCrimeBinding
import kotlin.random.Random

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CrimeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private var binding: FragmentCrimeBinding? = null
    private lateinit var crime: Crime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        crime = Crime()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        binding = FragmentCrimeBinding.inflate(layoutInflater, container, false)
        binding?.crimeDate?.apply {
            text = crime.date.toString()
            isEnabled = false
        }
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "onActivityCreated")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
        binding?.currentTime?.text = Random.Default.nextInt(10000).toString()
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        Log.d(TAG, "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach")
    }

    companion object {
        private const val TAG = "CrimeFragment"

        @JvmStatic
        fun newInstance(param1: String? = null, param2: String? = null) =
            CrimeFragment().apply {
                arguments = Bundle().apply {
                    param1?.let {
                        putString(ARG_PARAM1, param1)
                    }
                    param2?.let {
                        putString(ARG_PARAM2, param2)
                    }
                }
            }
    }
}