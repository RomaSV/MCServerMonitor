package com.example.mcservermonitor


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mcservermonitor.util.PREF_COORDINATE_X
import com.example.mcservermonitor.util.PREF_COORDINATE_Z
import kotlinx.android.synthetic.main.fragment_map_viewer_params.*


class MapViewerParamsFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = this.activity!!.getPreferences(Context.MODE_PRIVATE)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_viewer_params, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        map_viewer_x_input.setText(sharedPreferences.getInt(PREF_COORDINATE_X, 0).toString())
        map_viewer_y_input.setText(sharedPreferences.getInt(PREF_COORDINATE_Z, 0).toString())

        btn_show_map.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putInt(PREF_COORDINATE_X, map_viewer_x_input.text.toString().toInt())
            editor.putInt(PREF_COORDINATE_Z, map_viewer_y_input.text.toString().toInt())
            editor.apply()
            findNavController().navigate(R.id.action_mapViewerParamsFragment_to_mapViewerFragment)
        }
    }


}
