package hu.bme.aut.pictureteam.ui.main.userRatingList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.pictureteam.R
import hu.bme.aut.pictureteam.services.Categories
import hu.bme.aut.pictureteam.services.PictureInteractions
import kotlinx.android.synthetic.main.user_rating_list.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserRatingListView : Fragment() {
    private lateinit var adapter: UserRatingAdapter
    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.user_rating_list, container, false)

        initRecyclerView()

        updateRatings()

        return root
    }

    private fun updateRatings() {
        root.user_rating_refresh.isRefreshing = true
        lifecycleScope.launch(Dispatchers.IO) {
            val ratings = PictureInteractions.userRatings()
            withContext(Dispatchers.Main) {
                adapter.setRatings(ratings)
                root.user_rating_refresh.isRefreshing = false
            }
        }
    }

    private fun initRecyclerView() {
        root.user_rating_recyclerview.layoutManager = LinearLayoutManager(context)
        root.user_rating_refresh.setOnRefreshListener(::updateRatings)
        adapter = UserRatingAdapter()
        root.user_rating_recyclerview.adapter = adapter
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(): UserRatingListView {
            return UserRatingListView().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, 3)
                }

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        Categories.updateCategories()
                    }
                }
            }
        }
    }

}