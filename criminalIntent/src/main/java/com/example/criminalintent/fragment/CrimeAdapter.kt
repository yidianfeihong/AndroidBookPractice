package com.example.criminalintent.fragment

import android.icu.text.SimpleDateFormat
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalintent.R
import com.example.criminalintent.database.entity.Crime
import java.util.Locale
import java.util.UUID

class CrimeAdapter(var crimes: List<Crime>, var callback: Callbacks? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        Log.d(TAG, "init callback = $callback")
    }

    interface Callbacks {
        fun onCrimeSelected(id: UUID)
    }

    inner class CrimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var crime: Crime
        val titleView: TextView = itemView.findViewById(R.id.crime_title)
        val dateView: TextView = itemView.findViewById(R.id.crime_date)
        val crimeSolvedView: View = itemView.findViewById(R.id.crime_solved)

        init {
            itemView.setOnClickListener {
                Toast.makeText(itemView.context, "点击了${crime.title}", Toast.LENGTH_SHORT).show()
                callback?.onCrimeSelected(crime.id)
            }
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleView.text = crime.title
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val dateFormat = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.ENGLISH)
                dateView.text = dateFormat.format(crime.date)
            } else {
                dateView.text =
                    android.text.format.DateFormat.format("EEEE, MMMM dd, yyyy", crime.date)
            }
            if (crime.isSolved) {
                crimeSolvedView.visibility = View.GONE
            } else {
                crimeSolvedView.visibility = View.VISIBLE
            }
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        Log.d(TAG, "onViewAttachedToWindow holder = $holder")
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        Log.d(TAG, "onViewDetachedFromWindow holder = $holder")
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        Log.d(TAG, "onViewRecycled holder = $holder")
        super.onViewRecycled(holder)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        Log.d(TAG, "onCreateViewHolder:$viewType")
        val itemView =
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_crime_require_police,
                parent,
                false
            )
        return CrimeViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        Log.d(TAG, "onBindViewHolder:$position")
        val crime = crimes[position]
        (holder as? CrimeViewHolder)?.bind(crime)
    }

    override fun getItemCount(): Int {
        return crimes.size
    }

    companion object {
        private const val TAG = "CrimeAdapter"
    }
}