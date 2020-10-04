package hu.bme.aut.pictureteam

import android.annotation.SuppressLint
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

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val context = this

        lifecycleScope.launch {
            instruments = Api.create().getInstruments(20).instruments

            for (instrument in instruments) {
                instrument.name
                val rowItem = LayoutInflater.from(context).inflate(R.layout.instruments_row, null)
                rowItem.row_instrument_name.text = instrument.name
                rowItem.row_instrument_family.text = instrument.family
                list_of_rows.addView(rowItem)
            }
        }
    }
}