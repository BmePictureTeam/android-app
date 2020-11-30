package hu.bme.aut.pictureteam.ui.main.pictureList

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.pictureteam.R
import hu.bme.aut.pictureteam.services.Categories
import hu.bme.aut.pictureteam.services.PictureInteractions
import kotlinx.android.synthetic.main.image_list_tab.*
import kotlinx.android.synthetic.main.image_list_tab.view.*
import kotlinx.coroutines.*

class PictureListView : Fragment(), PictureAdapter.OnPictureSelectedListener {
    private lateinit var adapter: PictureAdapter
    private lateinit var root: View
    private var offset: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.image_list_tab, container, false)

        offset = 0

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
                offset,
                if (t.isBlank()) {
                    null
                } else {
                    t
                }
            )

            Log.d("pics", "${adapter.itemCount}")

            withContext(Dispatchers.Main) {
                if (offset == 0) {
                    adapter.setPictures(pictures.toMutableList())
                } else {
                    adapter.addPictures(pictures.toMutableList())
                }

                offset += PictureInteractions.PICTURE_LIMIT

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
        root.image_refresh.setOnRefreshListener {
            offset = 0
            updateImages()
        }
        adapter = PictureAdapter(this)
        root.recyclerview.adapter = adapter

        root.recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN))
                    if (offset != 0) {
                        updateImages()
                    }
            }
        })
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
