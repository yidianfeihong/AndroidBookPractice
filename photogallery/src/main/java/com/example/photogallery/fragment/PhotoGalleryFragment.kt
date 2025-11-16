package com.example.photogallery.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.example.photogallery.QueryPreferences
import com.example.photogallery.R
import com.example.photogallery.databinding.FragmentPhotoGalleryBinding
import com.example.photogallery.model.PhotoDetail
import com.example.photogallery.viewmodel.PhotoGalleryViewModel
import com.example.photogallery.work.PollWorker
import com.permissionx.mingdev.PermissionX
import java.util.concurrent.TimeUnit

/**
 * 只实现了获取精选图片集功能，其它搜索等接口不再一一尝试
 */
class PhotoGalleryFragment : Fragment() {

    private lateinit var binding: FragmentPhotoGalleryBinding

    companion object {
        private const val TAG = "PhotoGalleryFragment"
        private const val POLL_WORK = "POLL_WORK"

        @JvmStatic
        fun newInstance() = PhotoGalleryFragment()
    }

    private val viewModel: PhotoGalleryViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        lifecycle
        retainInstance = true
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        viewLifecycleOwner.lifecycle
//        viewLifecycleOwnerLiveData
        PreferenceManager.getDefaultSharedPreferences(context)
        binding = FragmentPhotoGalleryBinding.inflate(inflater, container, false)
        setUpRecyclerview()
        return binding.root
    }

    private fun setUpRecyclerview() {
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
        }
    }

    inner class PhotoAdapter(val photos: List<PhotoDetail>) :
        RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var imageView: ImageView = itemView.findViewById(R.id.image)

            fun bind(photo: PhotoDetail) {
                Glide.with(requireContext())
                    .load(photo.downloadUrls.tiny)
                    .into(imageView)
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val itemView = layoutInflater.inflate(R.layout.item_layout, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int
        ) {
            val photo = photos[position]
            holder.bind(photo)
        }

        override fun getItemCount(): Int {
            return photos.size
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        val searchView = menu.findItem(R.id.menu_item_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.d(TAG, "onQueryTextSubmit query =$query")
                viewModel.fetchPhotos(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d(TAG, "onQueryTextChange newText =$newText")
                return false
            }
        })
        val toggleItem = menu.findItem(R.id.menu_item_toggle_polling)
        if (QueryPreferences.isPolling(requireContext())) {
            toggleItem.title = getString(R.string.stop_polling)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    PermissionX.requestPermission(
                        requireActivity(),
                        Manifest.permission.POST_NOTIFICATIONS,
                        callback = null
                    )
                }
            }
        } else {
            toggleItem.title = getString(R.string.start_polling)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_clear -> {
                viewModel.fetchPhotos()
                true
            }

            R.id.menu_item_toggle_polling -> {
                val isPolling = QueryPreferences.isPolling(requireContext())
                if (isPolling) {
                    WorkManager.getInstance().cancelUniqueWork(POLL_WORK)
                    QueryPreferences.setPolling(requireContext(), false)
                } else {
                    //模拟器无法满足，即便网络是正常连接：state = NetworkState(isConnected=true, isValidated=false, isMetered=false, isNotRoaming=true)
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                    val periodicRequest = PeriodicWorkRequest
                        .Builder(PollWorker::class.java, 15, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .build()
//                    val oneTimeWorkRequest = OneTimeWorkRequestBuilder<PollWorker>()
//                        .setInitialDelay(1, TimeUnit.MINUTES)
//                        .setConstraints(constraints)
//                        .build()
                    WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                        POLL_WORK,
                        ExistingPeriodicWorkPolicy.KEEP,
                        periodicRequest
                    )
                    val connectivityManager = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val activeNetwork = connectivityManager.activeNetwork
                    val isConnected = activeNetwork != null
                    Log.d("NetworkCheck", "当前网络连接状态: $isConnected")
//                    WorkManager.getInstance(requireContext()).enqueue(oneTimeWorkRequest)
                    QueryPreferences.setPolling(requireContext(), true)
                }
                activity?.invalidateOptionsMenu()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.galleryItemLiveData.observe(
            viewLifecycleOwner,
            object : Observer<List<PhotoDetail>> {
                override fun onChanged(photos: List<PhotoDetail>) {
//                    for (photoResource in photos) {
//                        Log.d(TAG, "get photo:$photoResource")
//                    }
                    binding.recyclerView.adapter = PhotoAdapter(photos)
                }
            })
    }
}