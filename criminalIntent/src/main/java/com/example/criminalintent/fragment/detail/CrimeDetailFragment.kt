package com.example.criminalintent.fragment.detail

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.criminalintent.R
import com.example.criminalintent.database.entity.Crime
import com.example.criminalintent.databinding.FragmentCrimeBinding
import com.example.criminalintent.fragment.detail.dialog.DatePickerDialogFragment
import com.example.criminalintent.fragment.detail.dialog.DatePickerDialogFragment.Companion.ARG_DATE
import com.example.criminalintent.fragment.detail.dialog.DatePickerDialogFragment.Companion.REQUEST_KEY_DATE
import com.example.criminalintent.fragment.detail.dialog.TimePickerDialogFragment
import com.example.criminalintent.utils.PictureUtils
import com.example.criminalintent.viewmodel.CrimeDetailViewmodel
import com.permissionx.mingdev.PermissionX
import java.io.File
import java.util.Date
import java.util.Locale
import java.util.UUID

private const val ARG_CRIME_ID = "crime_id"

class CrimeDetailFragment : Fragment(), DatePickerDialogFragment.DatePickCallbacks {
    private var crimeId: UUID? = null
    private var binding: FragmentCrimeBinding? = null
    private lateinit var crime: Crime
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri
    private var photoWidth: Int? = null
    private var photoHeight: Int? = null
    private var photoBitmap: Bitmap? = null

    private val viewmodel by lazy {
        ViewModelProvider(this)[CrimeDetailViewmodel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        crimeId = arguments?.getSerializable(ARG_CRIME_ID) as? UUID
        viewmodel.loadCrime(crimeId)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        binding = FragmentCrimeBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        Log.d(TAG, "onCreateOptionsMenu")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected item = ${item.title}")
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
        viewmodel.crimeLiveData.observe(viewLifecycleOwner) { crime ->
            Log.d(TAG, "receive crime data = $crime")
            crime?.let {
                this.crime = crime
                photoFile = viewmodel.getCrimePhotoFile(crime)
                photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.androidbookpractice.criminalIntent.fileprovider",
                    photoFile
                )
                updateUI()
                updatePhotoView()
            }
        }
        binding?.photoPreview?.viewTreeObserver?.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                photoWidth = binding?.photoPreview?.width
                photoHeight = binding?.photoPreview?.height
                binding?.photoPreview?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun updateUI() {
        binding?.crimeTitle?.setText(crime.title)
        val formatDate =
            java.text.DateFormat.getDateInstance(java.text.DateFormat.LONG, Locale.getDefault())
                .format(crime.date)
        binding?.crimeDateButton?.text = formatDate
        if (!crime.suspect.isEmpty()) {
            binding?.crimeSuspectButton?.text = crime.suspect
        }
        binding?.crimeSolved?.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
        binding?.crimeTitle?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                crime.title = s.toString()
            }
        })
        binding?.crimeSolved?.setOnCheckedChangeListener { buttonView, isChecked ->
            crime.isSolved = isChecked
        }
        binding?.crimeDateButton?.setOnClickListener {
            DatePickerDialogFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeDetailFragment, REQUEST_CODE_DATE)
                show(this@CrimeDetailFragment.parentFragmentManager, DIALOG_DATE)
            }
        }
        binding?.crimeTimeButton?.setOnClickListener {
            TimePickerDialogFragment.newInstance().apply {
                timeChangeObserver = TimePicker.OnTimeChangedListener { view, hourOfDay, minute ->
                    Log.d(
                        TAG,
                        "time change：hourOfDay = $hourOfDay,minute = $minute"
                    )
                    binding?.crimeTimeButton?.text = "$hourOfDay:$minute"
                }
            }.show(parentFragmentManager, null)
        }
        parentFragmentManager.setFragmentResultListener(
            REQUEST_KEY_DATE,
            this
        ) { requestKey, result ->
            val date = result.getSerializable(ARG_DATE) as? Date
            if (date != null) {
                crime.date = date
                updateUI()
            }
        }
        binding?.crimeReportButton?.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }.also {
                val chooserIntent = Intent.createChooser(it, getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }
        binding?.crimeSuspectButton?.apply {
            val pickContactIntent =
                Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
//            pickContactIntent.addCategory(Intent.CATEGORY_HOME)
            val packageManager = requireActivity().packageManager
            val resolveActivity =
                packageManager.resolveActivity(pickContactIntent, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolveActivity == null) {
                isEnabled = false
            } else {
                isEnabled = true
                setOnClickListener {
                    startActivityForResult(pickContactIntent, REQUEST_CONTACT)
                }
            }
        }
        binding?.crimeCallButton?.setOnClickListener {
            requestPermission()
        }
        binding?.capturePhotoButton?.apply {
            val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolveActivity = requireActivity().packageManager.resolveActivity(
                captureIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
            if (resolveActivity == null) {
                isEnabled = false
            } else {
                setOnClickListener {
                    val cameraIntentActivities =
                        requireActivity().packageManager.queryIntentActivities(
                            captureIntent,
                            PackageManager.MATCH_DEFAULT_ONLY
                        )
                    for (cameraActivity in cameraIntentActivities) {
                        requireActivity().grantUriPermission(
                            cameraActivity.activityInfo.packageName,
                            photoUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        )
                    }
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(captureIntent, REQUEST_PHOTO)
                }
            }
        }
        binding?.photoPreview?.setOnClickListener {
            photoBitmap?.let {
                PhotoExpandDialogFragment.newInstance(photoFile.path)
                    .show(parentFragmentManager, null)
            }
        }
    }

    private fun requestPermission() {
        PermissionX.requestPermission(
            requireActivity(),
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE
        ) { allGranted, deniedList ->
            if (allGranted) {
                makeCall()
            } else {
                if (deniedList.isEmpty()) {
                    return@requestPermission
                }
                //轮流取被拒绝的权限
                val deniedPermission = deniedList[0]
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        deniedPermission
                    )
                ) {
                    showExplanationDialog("需要使用电话及联系人权限才能联系", "确定", null)
                } else {
                    showExplanationDialog(
                        "电话或联系人权限已被永久拒绝，请到应用设置中手动开启",
                        "去设置"
                    ) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.setData(
                            Uri.fromParts(
                                "package",
                                requireActivity().packageName,
                                null
                            )
                        )
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun showExplanationDialog(
        message: String,
        positiveButtonText: String,
        onPositiveClick: (() -> Unit)?
    ) {
        AlertDialog.Builder(requireActivity())
            .setTitle("权限说明")
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                onPositiveClick?.invoke()
                dialog.dismiss()
            }
            .setNegativeButton("取消", null)
            .create()
            .show()
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }

    private fun makeCall() {
        val suspect = crime.suspect
        if (suspect.isEmpty()) {
            Toast.makeText(context, "没有嫌疑人，无法直接打电话", Toast.LENGTH_SHORT).show()
            return
        }
        val queryFields = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
        )
        val cursor = requireActivity().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            queryFields,
            null,
            null,
            null
        )
        cursor?.use {
            if (it.count <= 0) {
                return@use
            }
            while (it.moveToNext()) {
                val idIndex = it.getColumnIndex(ContactsContract.Contacts._ID)
                val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val numberIndex =
                    it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val id = it.getString(idIndex)
                val name = it.getString(nameIndex)
                val number = it.getString(numberIndex)
                Log.d(TAG, "get contact result: id = $id name = $name number =$number")
                if (!name.isNullOrEmpty() && name == crime.suspect && !number.isNullOrEmpty()) {
                    val uri = "tel:$number".toUri()
                    val intent = Intent(Intent.ACTION_DIAL, uri)
                    if (intent.resolveActivity(requireActivity().packageManager) != null) {
                        startActivity(intent)
                    }
                    return@use
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult data = ${data?.data}")
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            REQUEST_CONTACT -> {
                if (data == null) {
                    return
                }
                val uri = data.data
                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                uri?.let { uri ->
                    val cursor =
                        requireActivity().contentResolver.query(
                            uri,
                            queryFields,
                            null,
                            null,
                            null
                        )
                    cursor?.use {
                        if (cursor.count <= 0) {
                            return
                        }
                        cursor.moveToFirst()
                        val columnIndex =
                            it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                        val suspect = cursor.getString(columnIndex)
                        binding?.crimeSuspectButton?.text = suspect
                        crime.suspect = suspect
                        viewmodel.saveCrime(crime)
                    }
                }
            }

            REQUEST_PHOTO -> {
                requireActivity().revokeUriPermission(
                    photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                updatePhotoView()
            }
        }
    }

    private fun updatePhotoView() {
        if (photoFile.exists()) {
            val destWidth = photoWidth
            val destHeight = photoHeight
            if (destWidth == null || destHeight == null) {
                binding?.photoPreview?.setImageBitmap(
                    PictureUtils.getScaledBitmap(
                        requireActivity(),
                        photoFile.path,
                    ).apply {
                        photoBitmap = this
                    }
                )
            } else {
                binding?.photoPreview?.setImageBitmap(
                    PictureUtils.getScaledBitmap(
                        photoFile.path,
                        destWidth,
                        destHeight
                    ).apply {
                        photoBitmap = this
                    }
                )
            }
        } else {
            binding?.photoPreview?.setImageDrawable(null)
        }
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()
        val suspect = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        return getString(
            R.string.crime_report,
            crime.title, dateString, solvedString, suspect
        )
    }

    override fun onStop() {
        super.onStop()
        viewmodel.saveCrime(crime)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        Log.d(TAG, "onDestroyView")
    }

    override fun onDateSelected(date: Date) {
        crime.date = date
        updateUI()
    }

    companion object {
        private const val TAG = "CrimeDetailFragment"
        private const val REQUEST_CODE_DATE = 0
        private const val REQUEST_CONTACT = 1
        private const val REQUEST_PERMISSIONS = 2
        private const val REQUEST_PHOTO = 3
        private const val DIALOG_DATE = "dialogDate"
        private const val DATE_FORMAT = "EEE, MMM, dd"


        @JvmStatic
        fun newInstance(crimeId: UUID) =
            CrimeDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_CRIME_ID, crimeId)
                }
            }
    }
}