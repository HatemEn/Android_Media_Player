package com.me.hatem.a08_kt_media_player.Controller


import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast

import com.me.hatem.a08_kt_media_player.Model.Song
import com.me.hatem.a08_kt_media_player.R
import com.me.hatem.a08_kt_media_player.Utilites.REQUEST_CODE_ASK_PERMISSIONS
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.loadstream_option_dialog.view.*

class MainActivity : AppCompatActivity() {

    lateinit var songAdapter: ArrayAdapter<Song>
    val songs: ArrayList<Song> = arrayListOf()
    lateinit var mp: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        songAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, songs)
        songsList.adapter = songAdapter
        preparePlayer()
        checkUserPermsions()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        when(id) {
            R.id.loadStream_option -> {
                val builder = AlertDialog.Builder(this)
                val view = layoutInflater.inflate(R.layout.loadstream_option_dialog, null)
                builder.setView(view)
                builder.setPositiveButton("Load") { dialog, which ->
                    val url = view.loadstreamUrl.text.toString()
                    val song = Song("LoadStream", url, "LoadStream", "*")
                    songs.add(song)
                    songAdapter.notifyDataSetChanged()
                }

                builder.setNegativeButton("Cancel") { dialog, which -> }
                builder.show()

            }

//            R.id.localSong_option -> {
//                val intent = Intent(Intent.ACTION_GET_CONTENT)
//                //intent.putExtra("CONTENT_TYPE", "*/*")
//                intent.setType("folder/*")
//                intent.addCategory(Intent.CATEGORY_DEFAULT)
//                startActivityForResult(Intent.createChooser(intent, "Choose directory"),LOCAL_SONG_REQUEST)
//            }
            R.id.exit_option -> { finish()}
        }

        return true
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            LOCAL_SONG_REQUEST -> {
                val data = data?.data.toString();
                println(data)
                val url = data
                val song = Song("LoadStream", url, "LoadStream", "*")
                songs.add(song)
                songAdapter.notifyDataSetChanged()

            }
        }
    }*/


    /* *
    * Check the permission for devices that have android SDK 23 and above
    * */
    fun checkUserPermsions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_CODE_ASK_PERMISSIONS)
                return
            }
        }

        loadAllSongs()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadAllSongs()
            } else {
                // Permission Denied
                Toast.makeText(this, "denial", Toast.LENGTH_SHORT)
                        .show()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /* *
    * Loading all songs in the device
    * */
    fun loadAllSongs() {
        val allSongsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC
        val cursor = contentResolver.query(allSongsUri, null, selection, null, null)
        with(cursor) {
            while (moveToNext()) {
                val title = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                val uri = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                val author = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val duration = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                println(duration)
                val newSong = Song(title, uri, author, duration)
                songs.add(newSong)
            }
        }
        cursor.close()
        songAdapter.notifyDataSetChanged()
    }

    fun preparePlayer() {
        var play = false
        mp = MediaPlayer()
        songsList.setOnItemClickListener { parent, view, position, id ->
            currentSongName.text =  songs[position].title
            playPauseBtn.setImageResource(android.R.drawable.ic_media_pause)
            if (mp.isPlaying) mp.stop()
            try {
                play = true
                mp = MediaPlayer()
                println(songs[position].url)
                mp.setDataSource(this, Uri.parse(songs[position].url))
                Log.e("err",songs[position].url)
                mp.prepare()
                mp.start()
                currentSongSeekBar.max = mp.duration
                val trackingSong = TrackingSong()
                trackingSong.start()
                currentSongSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        mp.seekTo(progress)
                    }
                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                })
                playPauseBtn.setOnClickListener {
                    if (play) {
                        mp.pause()
                        play = false
                        playPauseBtn.setImageResource(android.R.drawable.ic_media_play)
                    } else {
                        mp.start()
                        play = true
                        playPauseBtn.setImageResource(android.R.drawable.ic_media_pause)
                    }
                }
            } catch (e: Exception) {Log.e("Error","Something went wrong!")}

        }
    }

    inner class TrackingSong : Thread() {
        override fun run() {
            while (true) {
                try {
                    Thread.sleep(1000)
                } catch (e: Exception) {}
                runOnUiThread {
                    currentSongSeekBar.progress = mp.currentPosition
                }
            }
        }
    }



} //Main class
