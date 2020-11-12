package hu.bme.aut.pictureteam.services

import hu.bme.aut.pictureteam.models.Category
import java.util.*

object Categories {
    val categoryIdToTitle = hashMapOf<String, String>()
    val titleToCategoryId = hashMapOf<String, String>()
    var categories: List<Category> = listOf()

    suspend fun updateCategories() {
        categories = Api.getInstance().getCategories().categories

        for (c in categories) {
            categoryIdToTitle[c.id] = c.name
            titleToCategoryId[c.name.toLowerCase(Locale.getDefault())] = c.id
        }
    }
}