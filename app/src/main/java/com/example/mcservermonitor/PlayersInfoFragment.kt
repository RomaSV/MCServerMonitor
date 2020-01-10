package com.example.mcservermonitor


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mcservermonitor.ftp.getPlayerUIDs
import com.example.mcservermonitor.model.getUserNamesByUIDS
import kotlinx.android.synthetic.main.fragment_players_info.*
import kotlinx.coroutines.*


class PlayersInfoFragment : Fragment() {

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_players_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scope.launch {
            val data = withContext(Dispatchers.IO) {
                val ids = getPlayerUIDs()
                if (ids != null) {
                    return@withContext getUserNamesByUIDS(ids)
                } else {
                    return@withContext emptyArray<String>()
                }
            }

            val viewManager = LinearLayoutManager(context)
            val viewAdapter = PlayerListAdapter(data)
            players_recycle_view.apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }


}
