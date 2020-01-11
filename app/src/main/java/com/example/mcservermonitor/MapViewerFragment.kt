package com.example.mcservermonitor


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mcservermonitor.util.PREF_COORDINATE_X
import com.example.mcservermonitor.util.PREF_COORDINATE_Z
import kotlinx.android.synthetic.main.fragment_map_viewer.*


class MapViewerFragment : Fragment() {

    companion object {
        init {
            try {
                System.loadLibrary("GLES_aga")
            } catch (e: UnsatisfiedLinkError) {
                Log.e("GA", "GA not loaded: " + e.message)
            }
        }
    }

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        sharedPreferences = this.activity!!.getPreferences(Context.MODE_PRIVATE)
        return inflater.inflate(R.layout.fragment_map_viewer, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        map_viewer_progress_bar.max = 64
        map_viewer_map.progressBar = map_viewer_progress_bar
        map_viewer_map.desiredX = sharedPreferences.getInt(PREF_COORDINATE_X, 0)
        map_viewer_map.desiredZ = sharedPreferences.getInt(PREF_COORDINATE_Z, 0)
    }


}
