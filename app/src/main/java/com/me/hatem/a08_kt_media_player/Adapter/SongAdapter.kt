package com.me.hatem.a08_kt_media_player.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.me.hatem.a08_kt_media_player.Model.Song
import com.me.hatem.a08_kt_media_player.R


class SongAdapter(val context: Context, val songs: ArrayList<Song>, val onClick: (Song) -> Unit ) : RecyclerView.Adapter<SongAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.song_view_item, parent, false)
        return Holder(view, onClick)

    }

    override fun getItemCount(): Int {
        return songs.count()
    }

    override fun onBindViewHolder(holder: Holder?, position: Int) {
       holder?.onBindSong(songs[position])
    }

    inner class Holder(itemView: View?, val onClick: (Song) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val title   = itemView?.findViewById<TextView>(R.id.songTitleLabel)
        val author  = itemView?.findViewById<TextView>(R.id.songAuthorLabel)

        fun onBindSong(song: Song){
            title?.text     = song.title
            author?.text    = song.author

            itemView.setOnClickListener { onClick(song) }
        }
    }
}