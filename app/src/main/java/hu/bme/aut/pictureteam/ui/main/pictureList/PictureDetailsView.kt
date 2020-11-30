package hu.bme.aut.pictureteam.ui.main.pictureList

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import hu.bme.aut.pictureteam.R
import hu.bme.aut.pictureteam.models.Picture
import hu.bme.aut.pictureteam.services.Categories
import hu.bme.aut.pictureteam.services.PictureInteractions
import kotlinx.android.synthetic.main.image_detail_view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class PictureDetailsView : AppCompatActivity() {
    lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_detail_view)

        val id = intent.extras?.getString("id")

        if (id == null) {
            finish()
            return
        }

        this.id = id

        updatePicture()

        val context = this

        image_rating.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            if (!fromUser) {
                return@setOnRatingBarChangeListener
            }
            setLoading(true)
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    if (!PictureInteractions.rate(id, rating.toInt())) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "You cannot rate your own image.",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            ratingBar.rating = 0.0f
                        }
                    }
                } catch (e: Exception) {
                    Log.d("exception", e.message.toString())
                } finally {
                    withContext(Dispatchers.Main) {
                        setLoading(false)
                    }
                }
                withContext(Dispatchers.Main) {
                    updatePicture()
                }
            }

        }
    }

    private fun updatePicture() {
        lifecycleScope.launch {
            setLoading(true)
            try {
                val pic: Picture?
                var bitmap: Bitmap? = null
                withContext(Dispatchers.IO) {
                    pic = PictureInteractions.getById(id)

                    if (pic != null) {
                        bitmap = PictureInteractions.bitmap(pic)
                    }
                }

                if (pic == null) {
                    finish()
                    return@launch
                }

                image_large.setImageBitmap(bitmap)
                image_title.text = pic.title
                image_description.text = pic.description
                image_categories.text =
                    pic.categories.map { Categories.categoryName(it) }.joinToString("\n")
                image_total_rating.rating = pic.rating ?: 0.0f

            } catch (e: Exception) {
                Log.d("exception", e.message.toString())
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(v: Boolean) {
        if (v) {
            loading_bar.visibility = View.VISIBLE
        } else {
            loading_bar.visibility = View.INVISIBLE
        }
    }
}