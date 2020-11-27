package hu.bme.aut.pictureteam.models

data class Picture(
    val title: String,
    val description: String,
    val categories: List<String>,
    val id: String? = null,
    val date: String? = null,
    val rating: Float? = null,
    val ratingCount: Int? = null
)

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
