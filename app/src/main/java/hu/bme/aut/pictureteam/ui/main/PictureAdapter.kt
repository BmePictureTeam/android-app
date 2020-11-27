package hu.bme.aut.pictureteam.ui.main

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.scale
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.pictureteam.R
import hu.bme.aut.pictureteam.models.Picture
import hu.bme.aut.pictureteam.services.Categories
import kotlinx.android.synthetic.main.image_row.view.*
import kotlin.math.min

class PictureAdapter internal constructor(private val listener: OnPictureSelectedListener?) :
    RecyclerView.Adapter<PictureAdapter.PictureViewHolder>() {
    private var pictures: MutableList<Picture> = mutableListOf()
    private var bitmaps: HashMap<String, Bitmap> = hashMapOf()

    interface OnPictureSelectedListener {
        fun onPictureSelected(id: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.image_row, parent, false)
        return PictureViewHolder(view)
    }

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        val pic = pictures[position]
        holder.setPicture(pic, bitmaps[pic.id])
    }

    override fun getItemCount(): Int {
        return pictures.size
    }

    fun setPictures(pictures: MutableList<Picture>) {
        this.pictures = pictures
        this.bitmaps.clear()
        notifyDataSetChanged()
    }

    fun setBitmap(id: String, bitmap: Bitmap) {
        this.bitmaps[id] = bitmap
        notifyDataSetChanged()
    }

    inner class PictureViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        fun setPicture(picture: Picture, bitmap: Bitmap?) {
            itemView.row_image.setImageBitmap(bitmap?.scale(128,128))
            itemView.row_item_name.text = picture.title
            itemView.row_item_categories.text =
                formatList(
                    picture.categories.mapNotNull { Categories.categoryName(it) }, 3
                )
            itemView.row_image_rating.rating = picture.rating ?: 0.0f
            itemView.setOnClickListener { listener?.onPictureSelected(picture.id!!) }
        }
    }
}

private fun formatList(items: List<String>, max: Int? = null): String {
    if (items.isEmpty()) {
        return ""
    }

    if (max == null) {
        return items.joinToString(", ");
    }

    val s = items.slice(0 until min(items.size, max)).joinToString(", ")

    return if (max < items.size) {
        "$s, ..."
    } else {
        s
    }
}