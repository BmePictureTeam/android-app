package hu.bme.aut.pictureteam.services

import hu.bme.aut.pictureteam.models.ApiPicture
import hu.bme.aut.pictureteam.models.Category
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

data class ApiPicturesResponse (
    val pictures: List<ApiPicture>
)

data class ApiCategoriesResponse (
    val categories: List<Category>
)

data class ApiCreateImageRequestBody(
    val categories: List<String>,
    val description: String?,
    val title: String
)

interface Api {
    @GET("/images")
    suspend fun getPictures(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10,
        @Query("search") search: String? = null
    ): ApiPicturesResponse

    @GET("/categories")
    suspend fun getCategories(): ApiCategoriesResponse

    @GET("/images/{id}")
    suspend fun getPicture(
        @Path("id") id: String
    )

    @POST("/images")
    suspend fun createImage(
        @Body body: ApiCreateImageRequestBody
    )

    @Multipart
    @POST("/images/{id}")
    suspend fun uploadImage(
        @Path("id") id: String,
        @Part image: MultipartBody.Part
    )

    companion object {
        private const val URL: String = "https://api.temalab.cicum.icu"

        fun create(): Api {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(URL)
                .build()

            return retrofit.create(Api::class.java)
        }
    }
}

