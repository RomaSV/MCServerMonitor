package com.example.mcservermonitor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.player_list_item.view.*

class PlayerListAdapter(private val data: Array<String>) : RecyclerView.Adapter<PlayerListAdapter.PlayerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.player_list_item, parent, false)
        return PlayerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val playerName = data[position]
        holder.bind(playerName)
    }

    override fun getItemCount(): Int = data.size


    class PlayerViewHolder(private val _itemView: View) : RecyclerView.ViewHolder(_itemView) {

        fun bind(playerName: String) {
            _itemView.player_list_item_text.text = playerName
        }
    }
}