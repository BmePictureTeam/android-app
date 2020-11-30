package hu.bme.aut.pictureteam.ui.main.userRatingList

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.pictureteam.R
import hu.bme.aut.pictureteam.models.Picture
import hu.bme.aut.pictureteam.models.UserRating
import kotlinx.android.synthetic.main.image_row.view.*
import kotlinx.android.synthetic.main.user_rating_row.view.*
import kotlin.math.min

class UserRatingAdapter internal constructor() :
    RecyclerView.Adapter<UserRatingAdapter.UserRatingViewHolder>() {
    private var ratings: List<UserRating> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserRatingViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.user_rating_row, parent, false)
        return UserRatingViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserRatingViewHolder, position: Int) {
        val rating = ratings[position]
        holder.setRating(rating.name, rating.rating)
    }

    override fun getItemCount(): Int {
        return ratings.size
    }

    fun setRatings(ratings: List<UserRating>) {
        this.ratings = ratings
        notifyDataSetChanged()
    }

    inner class UserRatingViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        fun setRating(name: String, rating: Float) {
            itemView.user_name.text = name
            itemView.user_rating.rating = rating
        }
    }
}
