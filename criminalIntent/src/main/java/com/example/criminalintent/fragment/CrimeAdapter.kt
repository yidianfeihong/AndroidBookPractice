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
import com.example.criminalintent.CrimeApplication
import com.example.criminalintent.R
import com.example.criminalintent.database.Crime
import java.util.Locale

class CrimeAdapter(var crimes: List<Crime>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    open inner class CrimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private lateinit var crime: Crime
        val titleView: TextView = itemView.findViewById(R.id.crime_title)
        val dateView: TextView = itemView.findViewById(R.id.crime_date)

        init {
            itemView.setOnClickListener(this)
        }

        open fun bind(crime: Crime) {
            this.crime = crime
            titleView.text = crime.title
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val dateFormat = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.ENGLISH)
                dateView.text = dateFormat.format(crime.date)
            } else {
                dateView.text =
                    android.text.format.DateFormat.format("EEEE, MMMM dd, yyyy", crime.date)
            }
        }

        override fun onClick(v: View?) {
            Toast.makeText(
                CrimeApplication.context,
                "${crime.title} clicked!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    inner class CrimeRequirePoliceViewHolder(itemView: View) : CrimeViewHolder(itemView) {

        val requirePoliceView: View = itemView.findViewById(R.id.crime_solved)

        override fun bind(crime: Crime) {
            super.bind(crime)
            requirePoliceView.setOnClickListener {
                Toast.makeText(CrimeApplication.context, "已报警", Toast.LENGTH_SHORT).show()
            }
            if (crime.isSolved) {
                requirePoliceView.visibility = View.GONE
            } else {
                requirePoliceView.visibility = View.VISIBLE
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
        var viewHolder: RecyclerView.ViewHolder?
        when (viewType) {
            ITEM_STYLE_COMMON -> {
                val itemView =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_crime_common, parent, false)
                viewHolder = CrimeViewHolder(itemView)
            }

            ITEM_STYLE_REQUIRE_POLICE -> {
                val itemView =
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.list_item_crime_require_police,
                        parent,
                        false
                    )
                viewHolder = CrimeRequirePoliceViewHolder(itemView)
            }

            else -> {
                val itemView =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_crime_common, parent, false)
                viewHolder = CrimeViewHolder(itemView)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        Log.d(TAG, "onBindViewHolder:$position")
        if (position < crimes.size) {
            val crime = crimes[position]
            if (holder is CrimeViewHolder) {
                holder.bind(crime)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return ITEM_STYLE_REQUIRE_POLICE
//        return when (crimes[position].requiresPolice) {
//            true -> ITEM_STYLE_REQUIRE_POLICE
//            false -> ITEM_STYLE_COMMON
//        }
    }

    override fun getItemCount(): Int {
        return crimes.size
    }

    companion object {

        private const val TAG = "CrimeAdapter"
        private const val ITEM_STYLE_COMMON: Int = 0
        private const val ITEM_STYLE_REQUIRE_POLICE: Int = 1
    }
}