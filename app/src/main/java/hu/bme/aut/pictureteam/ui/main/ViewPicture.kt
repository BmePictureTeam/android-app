package hu.bme.aut.pictureteam.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import hu.bme.aut.pictureteam.R
import hu.bme.aut.pictureteam.models.Picture
import kotlinx.android.synthetic.main.detailed_view.*

class ViewPicture : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_detail_view)
        btnUpload.visibility = View.GONE
        imgbtnUpload.isEnabled = false

        tilName.editText?.keyListener = null
        tilName.editText?.setText(pictureSelected.title)
        tilDescription.editText?.setText(pictureSelected.description)
        tilDate.editText?.setText(pictureSelected.date)

    }

    companion object {
        lateinit var pictureSelected: Picture
        fun newInstance() {
//            pictureSelected = ListView.pictures[ListView.pictureSelected!!]
        }
    }
}