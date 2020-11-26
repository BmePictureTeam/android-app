package hu.bme.aut.pictureteam.services

import hu.bme.aut.pictureteam.models.Category
import kotlin.collections.HashMap

object Categories {
    private val categoryNames: HashMap<String, String> = hashMapOf()

    var categories: List<Category> = listOf()
        private set

    fun categoryName(id: String): String? {
        return categoryNames[id]
    }

    suspend fun updateCategories() {
        categories = Api.getInstance().getCategories().categories

        for (c in categories) {
            categoryNames[c.id] = c.name
        }
    }
}