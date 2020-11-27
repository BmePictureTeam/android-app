package hu.bme.aut.pictureteam.ui.main

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.pictureteam.R
import hu.bme.aut.pictureteam.models.Category
import kotlinx.android.synthetic.main.category_select_row.view.*

class PickCategoryAdapter(private val listener: CategorySelectionListener,
                          private val items: List<Category>
    ): RecyclerView.Adapter<PickCategoryAdapter.CategoryViewHolder>() {

    interface CategorySelectionListener {
        fun onCategorySelected(category: Category, selected: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.category_select_row,
                parent,
                false
            ),
            listener
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        Log.d("c", items[position].name)
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class CategoryViewHolder(
        itemView: View,
        private val listener: CategorySelectionListener
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(category: Category) {
            itemView.checkBox.text = category.name
            itemView.checkBox.setOnCheckedChangeListener { _, isChecked ->
                listener.onCategorySelected(category, isChecked)
            }
        }
    }
}
