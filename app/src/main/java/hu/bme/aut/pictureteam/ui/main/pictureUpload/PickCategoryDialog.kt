package hu.bme.aut.pictureteam.ui.main.pictureUpload

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.pictureteam.R
import hu.bme.aut.pictureteam.models.Category
import hu.bme.aut.pictureteam.services.Categories
import kotlinx.android.synthetic.main.pickcategorylayout.view.*

class PickCategoryDialog(private val listener: CategoryPickListener): DialogFragment(),
    PickCategoryAdapter.CategorySelectionListener {
    private lateinit var adapter: PickCategoryAdapter
    private val pickedCategories: HashSet<String> = hashSetOf()
    private lateinit var recyclerView: RecyclerView

    interface CategoryPickListener {
        fun onCategoryPicked(categories: List<String>)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Pick categories")
            .setView(getContentView())
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK") { dialogInterface, i ->
                listener.onCategoryPicked(pickedCategories.toList())
            }
            .create()


        return dialog
    }

    private fun getContentView(): View {
        val view = LayoutInflater.from(context).inflate(R.layout.pickcategorylayout, null)

        recyclerView = view.category_select_recyclerview
        adapter = PickCategoryAdapter(this, Categories.categories)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        return view
    }

    override fun onCategorySelected(category: Category, selected: Boolean) {
        if (selected) {
            pickedCategories.add(category.id)
        } else {
            pickedCategories.remove(category.id)
        }


    }
}