package com.armijoruiz.alberto.mykotlinapp

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v4.view.ViewPager
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.*
import android.support.v4.content.ContentResolverCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.armijoruiz.alberto.mykotlinapp.adapters.MyAdapter
import com.armijoruiz.alberto.mykotlinapp.fragments.FabFragment
import com.armijoruiz.alberto.mykotlinapp.fragments.MusicFragment
import com.armijoruiz.alberto.mykotlinapp.fragments.MusicPlayerFragment
import com.armijoruiz.alberto.mykotlinapp.other.FINISH
import com.armijoruiz.alberto.mykotlinapp.other.PLAYSONG
import com.armijoruiz.alberto.mykotlinapp.services.PlayMusicService
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.main_tab_activity.*
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainActivity : FragmentActivity() {


    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var canReadFromStorage : Boolean = false
    private var slidepanel:SlidingUpPanelLayout? = null
    val player = MusicPlayerFragment.newInstance() as Fragment


    companion object {
        private val fab_id:Long= 1
        val default_playlist:String = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString()
        val fab_playlist:String = MediaStore.Audio.Playlists.Members.getContentUri("external", fab_id).toString()
        val default_uri_string = "DEFAULT"

        fun getFabsPlaylistId():Long = fab_id

        fun addToPlaylist(audioId:Int,context : Context){
            Log.i("adding to playlist","adding song: "+audioId)
            var cr : ContentResolver = context.contentResolver
            var uri : Uri = MediaStore.Audio.Playlists.Members.getContentUri("external", fab_id)
            var cols = arrayOf("count(*)")
            var cursor : Cursor = cr.query(uri,cols,null, null,null)
            cursor.moveToFirst()
            val base = cursor.getInt(0)
            var values:ContentValues = ContentValues()
            values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, (base+audioId).toInt() )
            values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID,audioId)
            cr.insert(uri,values)
        }

        fun removeFromPlaylist(audioId: Int, context: Context){
            Log.i("removing from playlist","removing song: "+audioId)
            var cols = arrayOf("count(*)")
            var cr : ContentResolver = context.contentResolver
            var uri : Uri = MediaStore.Audio.Playlists.Members.getContentUri("external", fab_id)
            var cursor:Cursor = cr.query(uri,cols,null, null, null)
            cursor.moveToFirst()
            val base = cursor.getInt(0)
            cursor.close()
            var values:ContentValues = ContentValues()
            cr.delete(uri,MediaStore.Audio.Playlists.Members.AUDIO_ID+" = "+ audioId,null)

        }

        fun getSongIdFromMediaStore(songPath:String, context:Context):Long{
            var id :Long = 0

            var cr : ContentResolver = context.contentResolver
            var uri:Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            var selection:String = MediaStore.Audio.Media.DATA
            var selectionArgs = arrayOf(songPath)
            var projection = arrayOf(MediaStore.Audio.Media._ID)
            var sortOrder:String = MediaStore.Audio.Media.TITLE+" ASC"

            var cursor = cr.query(uri, projection,selection+"=?",selectionArgs,sortOrder)
            if(cursor != null){
                while(cursor.moveToNext()){
                    var index:Int = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
                    id = cursor.getLong(index)
                }
            }


            return id
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_tab_activity)

        setupPermissions()
        setupPlayer()

        slidepanel = findViewById(R.id.sliding_up_panel)
    }

    /*
    private fun setupSlidePanel(){
        slidepanel?.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener{

            // Función que se llama cuando se hace slide con el panel.
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
            }

            // Función que se llama cuando el panel cambia de estado.
            override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
                Log.i("slideuppanel", "sliding panel state changed")
                Log.i("slideuppanel", "newstate: "+newState)

                when(newState){
                    PanelState.EXPANDED ->{
                        playcardbutton?.visibility = View.INVISIBLE
                    }
                    PanelState.COLLAPSED ->{
                        playcardbutton?.visibility = View.VISIBLE
                    }
                    PanelState.DRAGGING ->{
                        if(previousState == PanelState.EXPANDED)
                            playcardbutton?.visibility = View.VISIBLE
                        else
                            playcardbutton?.visibility = View.INVISIBLE
                    }
                }
            }

        })
    }
    */

    private fun setupPlayer(){
        var fm = supportFragmentManager
        var transaction = fm.beginTransaction()
        transaction.replace(R.id.playerLayout,player)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun setupViews(){
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter
        tab_layout.setupWithViewPager(container)
    }

    // Funciones para pedir permisos.
    private fun setupPermissions(){
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val permission_2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if(Build.VERSION.SDK_INT >= 23){

            if(permission != PackageManager.PERMISSION_GRANTED || permission_2 != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
                setupPermissions()
            }else{
                canReadFromStorage = true
                setupViews()
            }
        }else{
            canReadFromStorage = true
            setupViews()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(PlayMusicService.serviceStarted)
            createMusicIntent(FINISH,0)

        stopService(Intent(this, PlayMusicService::class.java))
    }
    private fun createMusicIntent(action:String, position: Int = 0, seek_pos : Int = 0){
        var musicIntent = Intent(this, PlayMusicService::class.java)
        if(PlayMusicService.serviceStarted)
            musicIntent.setAction(action)
        else
            musicIntent.setAction(PLAYSONG)

        musicIntent.putExtra(MyAdapter.MUSICITEMPOS,position)
        musicIntent.putExtra(MusicFragment.PROGRESS, seek_pos)
        startService(musicIntent)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private var TAG = SectionsPagerAdapter::class.java.simpleName

        override fun getItem(position: Int): Fragment {
            Log.i(TAG, "getItem "+position)
            return when(position){
                0->MusicFragment.newInstance(canReadFromStorage)
                else -> {
                    return FabFragment.newInstance("path")
                }
            }
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence {
            return when(position){
                0-> "Música"
               else->{
                   return "Favoritos"
               }
            }
        }
    }


}
