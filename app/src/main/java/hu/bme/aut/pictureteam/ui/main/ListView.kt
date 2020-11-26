package hu.bme.aut.pictureteam.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListView : Fragment(), PictureAdapter.OnPictureSelectedListener {
    private lateinit var pageViewModel: PageViewModel
    private lateinit var adapter: PictureAdapter
    private lateinit var root: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.image_list_tab, container, false)
        pageViewModel.text.observe(viewLifecycleOwner, Observer<String> {
        })

        initRecyclerView()

        root.btnSearch.setOnClickListener { updateImages() }

//        updateImages()

        return root
    }

    private fun updateImages() {
        root.image_refresh.isRefreshing = true
        lifecycleScope.launch(Dispatchers.IO) {
            val images = PictureInteractions.search(0,null)
            withContext(Dispatchers.Main) {
                adapter.setPictures(images.toMutableList())
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
        var pictureSelected: Int? = null

        @JvmStatic
        fun newInstance(): ListView {
            return ListView().apply {
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

    override fun onPictureSelected(item: Int) {
        pictureSelected = item
        startActivity(Intent(context, ViewPicture::class.java))
    }
}