package hu.bme.aut.pictureteam.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.pictureteam.R
import kotlinx.android.synthetic.main.row_item.view.*

class PictureAdapter internal constructor(private val listener: OnPictureSelectedListener?) :
    RecyclerView.Adapter<PictureAdapter.PictureViewHolder>() {
    private val pictures: MutableList<String>

    interface OnPictureSelectedListener {
        fun onPictureSelected(s: String?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        return PictureViewHolder(view)
    }

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        val item = pictures[position]
        holder.nameTextView.text = pictures[position]
        holder.item = item
    }

    override fun getItemCount(): Int {
        return pictures.size
    }

    fun addItem(s: String) {
        pictures.add(s)
        notifyItemInserted(pictures.size - 1)
        //TODO("Not yet implemented")
    }

    inner class PictureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val picture = itemView.row_image
        val nameTextView = itemView.row_item_name
        val categoryTextView = itemView.row_item_name
        var item: String? = null

        init {
            itemView.setOnClickListener { listener?.onPictureSelected(item) }
        }
    }

    init {
        pictures = ArrayList()
    }
}