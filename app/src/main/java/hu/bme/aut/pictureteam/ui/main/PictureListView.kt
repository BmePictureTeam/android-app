package hu.bme.aut.pictureteam.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.pictureteam.R
import hu.bme.aut.pictureteam.services.Categories
import hu.bme.aut.pictureteam.services.PictureInteractions
import kotlinx.android.synthetic.main.image_list_tab.view.*
import kotlinx.android.synthetic.main.image_list.view.*
import kotlinx.android.synthetic.main.image_list_tab.*
import kotlinx.coroutines.*

class PictureListView : Fragment(), PictureAdapter.OnPictureSelectedListener {
    private lateinit var adapter: PictureAdapter
    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.image_list_tab, container, false)

        initRecyclerView()

        var textChangedJob: Job? = null

        root.search_text.addTextChangedListener {
            textChangedJob?.cancel()

            textChangedJob = lifecycleScope.launch {
                delay(500)
                updateImages()
            }
        }

        updateImages()

        return root
    }

    private fun updateImages() {
        root.image_refresh.isRefreshing = true
        lifecycleScope.launch(Dispatchers.IO) {
            val t = search_text.text.toString()
            val pictures = PictureInteractions.search(
                0,
                if (t.isBlank()) {
                    null
                } else {
                    t
                }
            )
            withContext(Dispatchers.Main) {
                adapter.setPictures(pictures.toMutableList())

                for (p in pictures) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val bitmap = PictureInteractions.bitmap(p)
                        withContext(Dispatchers.Main) {
                            adapter.setBitmap(p.id!!, bitmap)
                        }
                    }
                }

                root.image_refresh.isRefreshing = false
            }
        }
    }

    private fun initRecyclerView() {
        root.recyclerview.layoutManager = LinearLayoutManager(context)
        root.image_refresh.setOnRefreshListener(::updateImages)
        adapter = PictureAdapter(this)
        root.recyclerview.adapter = adapter
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(): PictureListView {
            return PictureListView().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, 1)
                }

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        Categories.updateCategories()
                    }
                }
            }
        }
    }

    override fun onPictureSelected(id: String) {
        val intent = Intent(context, PictureDetailsView::class.java)
        intent.putExtra("id", id);
        startActivity(intent)
    }
}