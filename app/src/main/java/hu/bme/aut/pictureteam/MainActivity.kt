package hu.bme.aut.pictureteam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import hu.bme.aut.pictureteam.models.Instrument
import hu.bme.aut.pictureteam.services.Api
import kotlinx.coroutines.launch
import retrofit2.await

class MainActivity : AppCompatActivity() {
    var instruments = listOf<Instrument>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            instruments = Api.create().getInstruments(2).instruments
            Log.d("asd", instruments.toString())

            for (instrument in instruments) {
                instrument.name
            }
        }

        setContentView(R.layout.activity_main)
    }
}