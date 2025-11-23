package com.example.photogallery.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.example.photogallery.databinding.FragmentVisibleBinding
import com.example.photogallery.model.PhotoDetail
import com.example.photogallery.viewmodel.WebViewModel

class PhotoGalleryWebFragment : VisibleFragment() {

    private lateinit var binding: FragmentVisibleBinding
    private var photo: PhotoDetail? = null

    companion object {

        const val TAG = "PhotoGalleryWebFragment"
        const val ARG_PHOTO = "photo"
        const val MAX_PROGRESS = 100
        fun newInstance(photo: PhotoDetail?) = PhotoGalleryWebFragment().apply {
            val bundle = Bundle()
            bundle.putSerializable(ARG_PHOTO, photo)
            arguments = bundle
        }
    }

    private val viewModel: WebViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVisibleBinding.inflate(
            inflater,
            container,
            false
        )
        initView()
        return binding.root
    }

    private fun initView() {
        binding.progressBar.max = MAX_PROGRESS
        binding.webView.apply {
            settings.javaScriptEnabled = true
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    if (newProgress == MAX_PROGRESS) {
                        binding.progressBar.visibility = View.GONE
                    } else {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.progressBar.progress = newProgress
                    }
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    (activity as AppCompatActivity).supportActionBar?.subtitle = title
                }
            }
            webViewClient = WebViewClient()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parseParams()
    }

    private fun parseParams() {
        photo = arguments?.getSerializable(ARG_PHOTO) as? PhotoDetail
        Log.i(TAG, "web photo = $photo")
        photo?.apply {
            if (linkPageUrl.isNotEmpty()) {
                binding.webView.loadUrl(linkPageUrl)
            }
        }
    }

    fun goBack(): Boolean {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
            return true
        }
        return false
    }
}