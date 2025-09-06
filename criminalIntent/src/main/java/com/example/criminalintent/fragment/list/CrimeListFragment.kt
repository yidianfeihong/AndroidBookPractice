package com.example.criminalintent.fragment.list

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalintent.R
import com.example.criminalintent.database.entity.Crime
import com.example.criminalintent.fragment.list.CrimeListAdapter.Callbacks
import com.example.criminalintent.viewmodel.CrimeListViewmodel


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CrimeListFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var crimeRecyclerView: RecyclerView
    private var callback: Callbacks? = null

    private var adapter: CrimeListAdapter = CrimeListAdapter(emptyList(), callback)

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
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        Log.d(TAG, "onCreateOptionsMenu")
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected item = ${item.title}")
        return when (item.itemId) {
            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewmodel.addCrime(crime)
                callback?.onCrimeSelected(crime.id)
                true
            }

            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view)
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
        crimeListViewmodel.crimeListLiveData.observe(viewLifecycleOwner) {
            Log.d(TAG, "crimeListLiveData notify crimes size = ${it.size}")
            updateUI(it)
        }
    }

    private fun updateUI(crimes: List<Crime>) {
        adapter = CrimeListAdapter(crimes, callback)
        adapter.submitList(crimes)
        crimeRecyclerView.adapter = CrimeListAdapter(crimes, callback)
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