package hu.bme.aut.pictureteam.services

import hu.bme.aut.pictureteam.models.Instrument
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class ApiInstrumentsResponse (
    val instruments: List<Instrument>
)

interface Api {
    @GET("/instruments")
    suspend fun getInstruments(@Query("count") count: Int = 10): ApiInstrumentsResponse

    companion object {
        private const val URL: String = "https://temalab.cicum.icu"

        fun create(): Api {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(URL)
                .build()

            return retrofit.create(Api::class.java)
        }
    }
}

