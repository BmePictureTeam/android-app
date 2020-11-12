package hu.bme.aut.pictureteam.models

import android.media.Image

data class Picture(
    val image: Image?,
    val name: String,
    val categories: MutableList<String>,
    val description: String,
    val date: String
)

data class ApiPicture(
    val categories: List<String>,
    val description: String,
    val title: String,
    val id: String
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
