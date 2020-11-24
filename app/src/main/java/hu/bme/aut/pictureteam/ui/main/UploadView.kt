package hu.bme.aut.pictureteam.ui.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import hu.bme.aut.pictureteam.MainActivity
import hu.bme.aut.pictureteam.R
import hu.bme.aut.pictureteam.models.Picture
import hu.bme.aut.pictureteam.services.Categories
import hu.bme.aut.pictureteam.services.Interactions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.detailed_view.*
import kotlinx.android.synthetic.main.detailed_view.view.*
import kotlinx.coroutines.launch
import java.util.*

class UploadView : Fragment() {
    private lateinit var imgbtn: ImageButton
    private lateinit var mainActivity: MainActivity
    private lateinit var pageViewModel: PageViewModel
    private var selectedImage: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.detailed_view, container, false)
        pageViewModel.text.observe(viewLifecycleOwner, {
        })

        root.tilDate.visibility = View.GONE

        root.imgbtnUpload.setOnClickListener {
            context?.let { it1 -> selectImage(it1) }
        }
        imgbtn = root.imgbtnUpload

        root.btnUpload.setOnClickListener {
            val title = tilName.editText?.text?.toString()

            if (!validateImage(title)) {
                return@setOnClickListener
            }

            val categoryNames =
                tilCategory.editText!!.text.toString().toLowerCase(Locale.getDefault())
                    .split(",").toList()
            val categoryIds = mutableListOf<String>()
            for (name in categoryNames) {
                Categories.titleToCategoryId[name]?.let { it1 -> categoryIds.add(it1) }
            }

            val description = tilDescription.editText?.text.toString()

            val picture = Picture(selectedImage, title, categoryIds, description, null)

            lifecycleScope.launch {
                val res = Interactions.upload(picture)
                if (res.code() != 204) {
                    Toast.makeText(
                        context,
                        "Upload failed: ${res.errorBody().toString()}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(context, "Upload succeeded", Toast.LENGTH_SHORT).show()
                    imgbtnUpload.setImageResource(R.drawable.placeholder)
                    tilName.editText?.text?.clear()
                    tilCategory.editText?.text?.clear()
                    tilDescription.editText?.text?.clear()
                }
            }
        }
        return root
    }

    private fun validateImage(title: String?): Boolean {
        if (selectedImage == null) {
            Toast.makeText(context, "Please select an image.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (title.isNullOrEmpty()) {
            Toast.makeText(context, "Name is required!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(mainActivity: MainActivity): UploadView {
            return UploadView().apply {
                this.mainActivity = mainActivity
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, 2)
                }
            }
        }
    }

    private fun selectImage(context: Context) {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Choose picture to upload")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(takePicture, 0)
                }
                options[item] == "Choose from Gallery" -> {
                    verifyStoragePermissions(mainActivity)
                    val pickPhoto =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhoto, 1)
                }
                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != AppCompatActivity.RESULT_CANCELED) {
            when (requestCode) {
                0 -> if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
                    selectedImage = data.extras!!["data"] as Bitmap?
                    imgbtn.setImageBitmap(selectedImage)
                }
                1 -> if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
                    val imgData: Uri? = data.data
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    if (imgData != null) {
                        val cursor: Cursor? = context?.contentResolver?.query(
                            imgData,
                            filePathColumn, null, null, null
                        )
                        if (cursor != null) {
                            cursor.moveToFirst()
                            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                            val picturePath: String = cursor.getString(columnIndex)
                            selectedImage = BitmapFactory.decodeFile(picturePath)
                            imgbtn.setImageBitmap(selectedImage)
                            cursor.close()
                        }
                    }
                }
            }
        }
    }

    // Storage Permissions
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    private fun verifyStoragePermissions(activity: Activity?) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }
}