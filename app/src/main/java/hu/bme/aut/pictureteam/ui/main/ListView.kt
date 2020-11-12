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
import hu.bme.aut.pictureteam.models.ApiPicture
import hu.bme.aut.pictureteam.models.Picture
import hu.bme.aut.pictureteam.services.Api
import hu.bme.aut.pictureteam.services.Categories
import hu.bme.aut.pictureteam.services.Categories.categoryIdToTitle
import kotlinx.android.synthetic.main.list_view.view.*
import kotlinx.android.synthetic.main.recycler_view.view.*
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
    ): View? {
        root = inflater.inflate(R.layout.list_view, container, false)
        pageViewModel.text.observe(viewLifecycleOwner, Observer<String> {
//            textView.text = it
        })

        root.btnSearch.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    apiImages = Api.getInstance().getPictures().pictures
                    for (p in apiImages) {
                        val pic = Picture(null, p.title, mutableListOf(), p.description, "")
                        //TODO: képet be kéne állítani/letölteni: getpicture
                        for (c in p.categories) {
                            categoryIdToTitle[c]?.let { it1 -> pic.categories.add(it1) }
                        }
                        images.add(pic)
                    }
                }
            }
        }

        initRecyclerView()

        return root
    }

    private fun initRecyclerView() {
        root.recyclerview.layoutManager = LinearLayoutManager(context)
        adapter = PictureAdapter(this)
        adapter.addItem("Macska")
        adapter.addItem("Cica")
        adapter.addItem("Kutya")
        adapter.addItem("Glaucus Atlanticus")
        root.recyclerview.adapter = adapter
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"
        var pictureSelected: Int? = null

        lateinit var apiImages: List<ApiPicture>
        lateinit var images: MutableList<Picture>

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

    override fun onPictureSelected(item: Int?) {
        pictureSelected = item
        startActivity(Intent(context, ViewPicture::class.java))
    }
}