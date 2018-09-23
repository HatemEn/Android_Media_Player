package com.me.hatem.a08_kt_media_player.Model

class Song(val title: String, val url: String, val author: String, val duration: String) {
    override fun toString(): String {
        return title
    }
}