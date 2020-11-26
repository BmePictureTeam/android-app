package hu.bme.aut.pictureteam.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.pictureteam.R
import hu.bme.aut.pictureteam.models.Picture
import hu.bme.aut.pictureteam.services.Categories
import kotlinx.android.synthetic.main.row_item.view.*
import kotlin.math.min

class PictureAdapter internal constructor(private val listener: OnPictureSelectedListener?) :
    RecyclerView.Adapter<PictureAdapter.PictureViewHolder>() {
    private var pictures: MutableList<Picture> = mutableListOf()

    interface OnPictureSelectedListener {
        fun onPictureSelected(item: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        return PictureViewHolder(view)
    }

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        holder.setPicture(position, pictures[position])
    }

    override fun getItemCount(): Int {
        return pictures.size
    }

    fun addPicture(p: Picture) {
        pictures.add(p)
        notifyItemInserted(pictures.size - 1)
    }

    fun setPictures(pictures: MutableList<Picture>) {
        this.pictures = pictures
        notifyDataSetChanged()
    }

    fun clear() {
        pictures.clear()
        notifyDataSetChanged()
    }

    inner class PictureViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        fun setPicture(pos: Int, picture: Picture) {
            itemView.row_image.setImageBitmap(picture.image)
            itemView.row_item_name.text = picture.title
            itemView.row_item_categories.text =
                formatList(
                    picture.categories.mapNotNull { Categories.categoryName(it) }, 3
                )
            itemView.row_image_rating.rating = picture.rating ?: 0.0f
            itemView.setOnClickListener { listener?.onPictureSelected(pos) }
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

    return items.slice(0 until min(items.size, max)).joinToString(", ") + ", ..."
}