package hu.bme.aut.pictureteam.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import hu.bme.aut.pictureteam.R
import kotlinx.android.synthetic.main.detailed_view.*

class ViewPicture : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailed_view)
        btnUpload.visibility = View.GONE
        imgbtnUpload.isEnabled = false

        val pictureSelected = ListView.images[ListView.pictureSelected!!]

        tilName.editText?.keyListener = null
        tilName.editText?.setText(pictureSelected.name)
        tilDescription.editText?.setText(pictureSelected.description)
        tilDate.editText?.setText(pictureSelected.date)

    }
}