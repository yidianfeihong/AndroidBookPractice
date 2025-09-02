package com.example.criminalintent.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalintent.R
import com.example.criminalintent.database.entity.Crime
import com.example.criminalintent.fragment.CrimeAdapter.Callbacks
import com.example.criminalintent.viewmodel.CrimeListViewmodel


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CrimeListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CrimeListFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var crimeRecyclerView: RecyclerView
    private var callback: Callbacks? = null

    private val crimeListViewmodel: CrimeListViewmodel by lazy {
        ViewModelProvider(this)[CrimeListViewmodel::class.java]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as? Callbacks
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view)
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewmodel.crimeList.observe(viewLifecycleOwner, {
            Log.d(TAG, "receive crimes size = ${it.size}")
            updateUI(it)
        })
    }

    private fun updateUI(crimes: List<Crime>) {
        crimeRecyclerView.adapter = CrimeAdapter(crimes, callback)
    }

    companion object {
        private const val TAG = "CrimeListFragment"

        @JvmStatic
        fun newInstance(param1: String? = null, param2: String? = null) =
            CrimeListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}