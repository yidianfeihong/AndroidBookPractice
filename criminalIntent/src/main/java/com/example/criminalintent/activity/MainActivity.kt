package com.example.criminalintent.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.criminalintent.R
import com.example.criminalintent.databinding.ActivityMainBinding
import com.example.criminalintent.fragment.list.CrimeListAdapter
import com.example.criminalintent.fragment.detai.CrimeDetailFragment
import com.example.criminalintent.fragment.list.CrimeListFragment
import java.util.UUID

class MainActivity : AppCompatActivity(), CrimeListAdapter.Callbacks {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "MainActivity onCreate")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.replaceFragment.setOnClickListener {
            replaceFragment()
        }

        binding.showFragment.setOnClickListener {
            showFragment()
        }

        binding.hideFragment.setOnClickListener {
            hideFragment()
        }
        addFragment()
    }

    private fun replaceFragment() {
        val crimeListFragment = CrimeListFragment.Companion.newInstance()
        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container, crimeListFragment
        ).addToBackStack("replace_transaction")
            .commit()
    }

    private fun showFragment() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        fragment?.let {
            supportFragmentManager.beginTransaction().show(
                it
            ).commit()
        }
    }

    private fun hideFragment() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        fragment?.let {
            supportFragmentManager.beginTransaction().hide(
                it
            ).commit()
        }
    }

    private fun addFragment() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (fragment == null) {
            val transaction = supportFragmentManager.beginTransaction()
            val crimeFragment = CrimeListFragment.newInstance()
            transaction.add(R.id.fragment_container, crimeFragment)
            transaction.commit()
        }
    }

    override fun onCrimeSelected(id: UUID) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CrimeDetailFragment.newInstance(id))
            .addToBackStack("move_to_detail")
            .commit()
    }

    companion object {
        const val TAG = "MainActivity"
    }

}