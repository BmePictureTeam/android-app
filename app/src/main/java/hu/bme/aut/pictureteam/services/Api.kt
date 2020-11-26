package hu.bme.aut.pictureteam.services

import hu.bme.aut.pictureteam.models.ApiPicture
import hu.bme.aut.pictureteam.models.Category
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

data class ApiSearchResponse(
    val images: List<ApiPicture>
)

data class ApiCategoriesResponse(
    val categories: List<Category>
)

data class ApiCreateImageRequestBody(
    val categories: List<String>,
    val description: String?,
    val title: String
)

data class ApiCreateImageResponse(
    val id: String
)

data class ApiLoginBody(
    val email: String,
    val password: String
)

data class ApiLoginResponse(
    val token: String
)

data class ApiPictureRatingResponse(
    val average: Float,
    val rating_count: Int
)

interface Api {
    @POST("/auth/login")
    suspend fun login(@Body login: ApiLoginBody): ApiLoginResponse

    @GET("/images")
    suspend fun searchPictures(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10,
        @Query("search") search: String? = null
    ): ApiSearchResponse

    @GET("/categories")
    suspend fun getCategories(): ApiCategoriesResponse

    @GET("/images/{id}")
    suspend fun getPicture(@Path("id") id: String): ResponseBody

    @POST("/images")
    suspend fun createPicture(@Body body: ApiCreateImageRequestBody): ApiCreateImageResponse

    @Multipart
    @POST("/images/{id}")
    suspend fun uploadPicture(
        @Path("id") id: String,
        @Part image: MultipartBody.Part
    ): retrofit2.Response<Unit>

    @GET("/images/{id}/rating")
    suspend fun getPictureRating(@Path("id") id: String): ApiPictureRatingResponse

    companion object {
        private const val URL: String = "https://api.temalab.cicum.icu"
        private var api: Api? = null

        fun getInstance(): Api {
            if (api == null) {
                createApi()
            }
            return api as Api
        }

        fun setToken(token: String?) {
            createApi(token)
        }

        private fun createApi(token: String? = null) {
            val client = OkHttpClient.Builder()
                .addInterceptor(ServiceInterceptor(token))
                .build()

            val retrofitBuilder = Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(URL)

            api = retrofitBuilder.build().create(Api::class.java)
        }
    }
}

class ServiceInterceptor(
    private val token: String? = null
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        if (request.header("No-Authentication") == null) {
            //val token = getTokenFromSharedPreference();
            //or use Token Function
            if (!token.isNullOrEmpty()) {
                val finalToken = "Bearer $token"
                request = request.newBuilder()
                    .addHeader("Authorization", finalToken)
                    .build()
            }
        }

        return chain.proceed(request)
    }

}
