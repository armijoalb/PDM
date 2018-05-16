package com.armijoruiz.alberto.mykotlinapp

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContentResolverCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.armijoruiz.alberto.mykotlinapp.fragments.FabFragment
import com.armijoruiz.alberto.mykotlinapp.fragments.MusicFragment

import kotlinx.android.synthetic.main.main_tab_activity.*
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainActivity : AppCompatActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var canReadFromStorage : Boolean = false


    companion object {
        private val fab_id:Long= 1

        fun getFabsPlaylistId():Long = fab_id

        fun addToPlaylist(audioId:Int,context : Context){
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
    }

    private fun setupViews(){
        setSupportActionBar(toolbar)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter
        tab_layout.setupWithViewPager(container)
    }

    // Funciones para pedir permisos.
    private fun setupPermissions(){
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if(Build.VERSION.SDK_INT >= 23){
            if(permission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
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
