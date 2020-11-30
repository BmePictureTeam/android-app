package hu.bme.aut.pictureteam.ui.main.pictureUpload

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import hu.bme.aut.pictureteam.R
import hu.bme.aut.pictureteam.models.Picture
import hu.bme.aut.pictureteam.services.PictureInteractions
import hu.bme.aut.pictureteam.util.resize
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.image_upload_view.*
import kotlinx.android.synthetic.main.image_upload_view.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class PictureUploadView : Fragment(), PickCategoryDialog.CategoryPickListener {
    private lateinit var imgbtn: ImageButton

    private var loading: Boolean = false
    private var selectedImage: Bitmap? = null
    private var selectedCategories: List<String> = listOf()
    private val permissionResults: Channel<Boolean> = Channel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.image_upload_view, container, false)

        root.tilDate.visibility = View.GONE

        root.imgbtnUpload.setOnClickListener {
            if (loading) {
                return@setOnClickListener
            }

            context?.let { it1 -> selectImage(it1) }
        }
        imgbtn = root.imgbtnUpload

        root.btnUpload.setOnClickListener {
            val title = tilName.editText?.text?.toString()

            if (!validateImage(title)) {
                return@setOnClickListener
            }

            val description = tilDescription.editText?.text.toString()

            val picture = Picture(title!!, description, selectedCategories)

            lifecycleScope.launch {
                val toastMessage = try {
                    setLoading(true)
                    withContext(Dispatchers.IO) {
                        PictureInteractions.upload(picture, selectedImage!!.resize(1920, 1080))
                    }
                    reset()
                    "Upload succeeded"
                } catch (e: Exception) {
                    "Upload failed: ${e.message}"
                } finally {
                    setLoading(false)
                }

                Toast.makeText(
                    context,
                    toastMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        root.btnSelectCategories.setOnClickListener {
            val dialog = PickCategoryDialog(this)


            dialog.show(
                childFragmentManager,
                "PICK_CATEGORY_DIALOG"
            )
        }

        return root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        _permissions: Array<out String>,
        grantResults: IntArray
    ) {
        for (res in grantResults) {
            if (res != PackageManager.PERMISSION_GRANTED) {
                lifecycleScope.launch {
                    permissionResults.send(false)
                }
                return
            }
        }

        lifecycleScope.launch {
            permissionResults.send(true)
        }
    }

    private fun setLoading(v: Boolean) {
        loading = v
        if (v) {
            btnUpload.startAnimation()
            tilName.isEnabled = false
            tilDescription.isEnabled = false
        } else {
            btnUpload.revertAnimation();
            tilName.isEnabled = true
            tilDescription.isEnabled = true
        }
    }

    private fun reset() {
        imgbtnUpload.setImageResource(R.drawable.placeholder)
        tilName.editText?.text?.clear()
        tilDescription.editText?.text?.clear()
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
        fun newInstance(): PictureUploadView {
            return PictureUploadView().apply {
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
                    lifecycleScope.launch {
                        if (!requestPermissions()) {
                            Toast.makeText(context, "Please grant permissions.", Toast.LENGTH_SHORT)
                                .show()
                            return@launch
                        }
                        val pickPhoto =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(pickPhoto, 1)
                    }
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
    private val permissionCodes = arrayOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    private suspend fun requestPermissions(): Boolean {
        requestPermissions(
            permissionCodes,
            1
        )

        return permissionResults.receive()
    }

    override fun onCategoryPicked(categories: List<String>) {
        Log.d("categories", categories.joinToString(","))

        selectedCategories = categories
    }

//    private fun permission() : Int? {
//        return context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.WRITE_EXTERNAL_STORAGE) }
//    }
}