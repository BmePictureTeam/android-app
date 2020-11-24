package hu.bme.aut.pictureteam.models

import android.graphics.Bitmap

data class Picture(
    val image: Bitmap?,
    val title: String?,
    val categories: MutableList<String>,
    val description: String,
    val date: String?
){
    fun categoriesToString(): String {
        var rv = ""

        for (category in categories) {
            rv += category
            rv += " "
        }

        return rv
    }
}

data class ApiPicture(
    val categories: List<String>,
    val date: String,
    val description: String?,
    val id: String,
    val title: String
)

/*
{
    "images": [
    {
        "categories": [
        "497f6eca-6276-4993-bfeb-53cbbbba6f08"
        ],
        "description": "string",
        "title": "string"
    }
    ]
}
*/
