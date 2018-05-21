package com.armijoruiz.alberto.mykotlinapp.fragments


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.armijoruiz.alberto.mykotlinapp.MainActivity
import com.armijoruiz.alberto.mykotlinapp.R
import com.armijoruiz.alberto.mykotlinapp.adapters.MyAdapter
import com.armijoruiz.alberto.mykotlinapp.interfaces.CustomFabListener
import com.armijoruiz.alberto.mykotlinapp.interfaces.CustomMusicListener
import com.armijoruiz.alberto.mykotlinapp.interfaces.CustomOnItemClickListener
import com.armijoruiz.alberto.mykotlinapp.other.PLAYSONG
import com.armijoruiz.alberto.mykotlinapp.services.PlayMusicService
import com.armijoruiz.alberto.mykotlinapp.structures.Song


/**
 * A simple [Fragment] subclass.
 * Use the [FabFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FabFragment : Fragment(), CustomOnItemClickListener, CustomFabListener {


    private var path: String? = null
    private var TAG = FabFragment::class.java.simpleName
    private var songs:ArrayList<Song> = ArrayList()
    private lateinit var adapter:MyAdapter
    private lateinit var recycler: RecyclerView


    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
                FabFragment().apply {
                    arguments = Bundle().apply {
                        putString("PATH", param1)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            path = it.getString("PATH")
        }
    }

    override fun onResume() {
        super.onResume()
        updateList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        var mView :View = inflater.inflate(R.layout.fragment_fab, container, false)
        PlayMusicService.customFabListener = this
        Log.i(TAG,"onCreateView")
        getFabList()
        recycler = mView.findViewById(R.id.fab_recycler)
        adapter = MyAdapter(songs,this)
        recycler.adapter = adapter
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(context)

        return mView
    }

    override fun onFabListChanged() {
        updateList()
    }

    fun updateList(){
        Log.i(TAG,"updating song's list")
        songs = ArrayList()
        getFabList()
        adapter = MyAdapter(songs,this)
        recycler.adapter = adapter
    }

    private fun getFabList(){
        var uri : Uri = Uri.parse(MainActivity.fab_playlist)
        val contentR = context.contentResolver
        val cursor = contentR.query(uri,null, null, null, null)

        if(cursor != null && cursor.moveToFirst()){
            do{
                var track_id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID))
                var uri_content = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                var trackProjection = arrayOf( MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.DATA, MediaStore.Audio.Media._ID)
                var selection = MediaStore.Audio.Media._ID + "=?"
                var selectionArgs = arrayOf(""+track_id)
                var mediaCursor = contentR.query(uri_content,trackProjection,selection,selectionArgs,null)
                Log.i(TAG,"mediaCursorCreated")
                if(mediaCursor != null){
                    mediaCursor.moveToNext()
                    var title = mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    var artist = mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    var duration = mediaCursor.getInt(mediaCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)) / 1000
                    var path = mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    var id = mediaCursor.getLong(mediaCursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    songs.add(Song(artist,title,path,duration,id))
                }
            }while (cursor.moveToNext())
        }

        Log.i(TAG,"number of songs: "+ songs.size)
    }

    override fun onItemClick(position: Int) {
        var musicIntent = Intent(context,PlayMusicService::class.java)
        musicIntent.setAction(PLAYSONG)
        musicIntent.putExtra(MainActivity.default_uri_string,MainActivity.fab_playlist)
        musicIntent.putExtra(MyAdapter.MUSICITEMPOS,position)
        activity.startService(musicIntent)
    }



}
