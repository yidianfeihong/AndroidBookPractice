package com.example.criminalintent.fragment.detai

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.example.criminalintent.R
import com.example.criminalintent.utils.PictureUtils
import androidx.core.graphics.drawable.toDrawable


class PhotoExpandDialogFragment : DialogFragment() {

    private var imagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePath = arguments?.getString(ARG_IMAGE_PATH, null)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_photo_expand, container, false)
        val picturePreview = view.findViewById<ImageView>(R.id.picture_preview)
        imagePath?.let {
            picturePreview?.post {
                picturePreview.setImageBitmap(
                    PictureUtils.getScaledBitmap(
                        it,
                        picturePreview.width,
                        picturePreview.height
                    )
                )
            }
        }
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        return view
    }

    companion object {
        const val ARG_IMAGE_PATH = "imagePath"

        @JvmStatic
        fun newInstance(path: String): PhotoExpandDialogFragment {
            return PhotoExpandDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_IMAGE_PATH, path)
                }
            }
        }
    }
}