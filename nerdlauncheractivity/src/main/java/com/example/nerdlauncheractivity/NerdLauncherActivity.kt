package com.example.nerdlauncheractivity

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nerdlauncheractivity.databinding.ActivityNerdLauncherBinding

class NerdLauncherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNerdLauncherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNerdLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setUpAdapter()
    }

    private fun setUpAdapter() {
        val queryIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val activities = packageManager.queryIntentActivities(
            queryIntent,
            0
        )
        Log.d(TAG, "queryIntentActivities size = ${activities.size}")
        activities.sortWith(Comparator<ResolveInfo> { a, b ->
            String.CASE_INSENSITIVE_ORDER.compare(
                a.loadLabel(packageManager).toString(),
                b.loadLabel(packageManager).toString()
            )
        })
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@NerdLauncherActivity)
            addItemDecoration(
                DividerItemDecoration(
                    this@NerdLauncherActivity,
                    LinearLayout.VERTICAL
                )
            )
            adapter = ActivityAdapter(activities)
        }
    }


    inner class ActivityAdapter(val activities: List<ResolveInfo>) :
        RecyclerView.Adapter<ActivityViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ActivityViewHolder {
            val itemView =
                layoutInflater.inflate(R.layout.item_layout, parent, false)
            return ActivityViewHolder(itemView)
        }

        override fun onBindViewHolder(
            holder: ActivityViewHolder,
            position: Int
        ) {
            val resolveInfo = activities[position]
            holder.bindActivity(resolveInfo)
        }

        override fun getItemCount(): Int {
            return activities.size
        }
    }

    class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var iconImageView: ImageView = itemView.findViewById(R.id.appIconView)
        private var nameTextView: TextView = itemView.findViewById(R.id.appNameView)
        private lateinit var resolveInfo: ResolveInfo

        init {
            itemView.setOnClickListener { view ->
                val content = view.context
                val activityInfo = resolveInfo.activityInfo
                val intent = Intent(Intent.ACTION_MAIN).setClassName(
                    activityInfo.applicationInfo.packageName,
                    activityInfo.name
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                content.startActivity(intent)
            }
        }

        fun bindActivity(resolveInfo: ResolveInfo) {
            this.resolveInfo = resolveInfo
            val packageManager = itemView.context.packageManager
            nameTextView.text = resolveInfo.loadLabel(packageManager)
            iconImageView.setImageDrawable(resolveInfo.loadIcon(packageManager))
        }
    }


    companion object {
        private const val TAG = "NerdLauncherActivity"
    }

}