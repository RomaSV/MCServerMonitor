package com.example.mcservermonitor


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_main_menu.*
import kotlinx.android.synthetic.main.fragment_map_viewer.*


class MainMenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_map_viewer.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_mapViewerParamsFragment)
        }
        btn_players_info.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_playersInfoFragment)
        }
    }

}
