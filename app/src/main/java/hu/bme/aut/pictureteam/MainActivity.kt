package hu.bme.aut.pictureteam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import hu.bme.aut.pictureteam.models.Instrument
import hu.bme.aut.pictureteam.services.Api
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.instruments_row.view.*
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    var instruments = listOf<Instrument>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val context = this

        lifecycleScope.launch {
            instruments = Api.create().getInstruments(20).instruments
            //test
            Log.d("asd", instruments.toString())

            for (instrument in instruments) {
                //TODO: list instruments on screen
                instrument.name
                //layout inflate, list of rows
                val rowItem = LayoutInflater.from(context).inflate(R.layout.instruments_row, null)
                rowItem.row_instrument_name.text = instrument.name
                rowItem.row_instrument_family.text = instrument.family
                list_of_rows.addView(rowItem)
            }
        }
    }
}