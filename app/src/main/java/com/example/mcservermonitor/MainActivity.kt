package com.example.mcservermonitor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.max = 64
        val mapView = findViewById<MapView>(R.id.mapView)
        mapView.progressBar = progressBar

    }

}
