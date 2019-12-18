package com.example.mcservermonitor


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_viewer, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        map_viewer_progress_bar.max = 64
        map_viewer_map.progressBar = map_viewer_progress_bar
    }


}
