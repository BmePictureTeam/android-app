package hu.bme.aut.pictureteam.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import hu.bme.aut.pictureteam.R
import hu.bme.aut.pictureteam.ui.main.pictureList.PictureListView
import hu.bme.aut.pictureteam.ui.main.pictureUpload.PictureUploadView
import hu.bme.aut.pictureteam.ui.main.userRatingList.UserRatingListView

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2,
    R.string.tab_text_3,
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(
    private val context: Context,
    fm: FragmentManager
) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return when (position) {
            2 -> {
                UserRatingListView.newInstance()
            }
            1 -> {
                PictureUploadView.newInstance()
            }
            0 -> {
                PictureListView.newInstance()
            }
            else -> throw ArrayIndexOutOfBoundsException("No such tab with given index")
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 3
    }
}