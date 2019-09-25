package com.example.mcservermonitor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val xInput = findViewById<EditText>(R.id.xCoord)
        xInput.addTextChangedListener(CoordinatesChangedListener(0))
        val zInput = findViewById<EditText>(R.id.zCoord)
        zInput.addTextChangedListener(CoordinatesChangedListener(1))
    }

    inner class CoordinatesChangedListener(val coordinate: Int): TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // do nothing
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s.toString().isNotEmpty()) {
                val value = s.toString().toInt()
                if (value in 0..15) {
                    val mapView = findViewById<MapView>(R.id.mapView)
                    if (coordinate == 0) {
                        mapView.updateCoords(value, mapView.currentChunkZ)
                    } else {
                        mapView.updateCoords(mapView.currentChunkX, value)
                    }
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {
            // do nothing
        }

    }
}
