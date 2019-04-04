package com.example.flickrbrowser

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : BaseActivity(), GetRawData.OnDownloadComplete,
    GetFlickrJsonData.OnDataAvailable, RecyclerItemClickListener.OnRecyclerClickListener {
//Du kan göra om kod till Java genom Tools->Kotlin->Show ByteCode -> (I högra navigatorn) Decompile.
    private val flickrRecyclerViewAdapter = FlickrRecyclerViewAdaptor(ArrayList())
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activateToolbar(false) //vill inte att homeknappen ska visas för vi är redan på home vyn
        //RecyclerView doesnt take care of handling the layouts, thats done by the layoutManager im specifying here,
        //RecyclerView gör alltså inte mycket annat än att återanvända vyer! Och allt annat lämnas till andra objekt.
        //So the data and the view to display are provided by a recycler adapter, the layout is performed by a layout manager, and the views themselves live in a view holder.
        //So by delegating like this the RecyclerView becomes far more flexible than a list view and performs a lot better.

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.addOnItemTouchListener(RecyclerItemClickListener(this, recycler_view, this))
        recycler_view.adapter = flickrRecyclerViewAdapter


        Log.d(TAG, "onCreate ends")
    }

    override fun onResume() {
        Log.d(TAG, ".onResume: starts")
        super.onResume()
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val queryResult = sharedPref.getString(FLICKR_QUERY, "")
        if (queryResult.isNotEmpty()) {
            val url = createUri("https://api.flickr.com/services/feeds/photos_public.gne", queryResult, "en-us", true)
            val getRawData = GetRawData(this)
            // getRawData.setDownloadCompleteListener(this)
            getRawData.execute(url)
        }
        Log.d(TAG, ".onResume: ends")
    }

    private fun createUri(baseUrl: String, searchCriteria: String, lang: String, matchAll: Boolean): String {
        Log.d(TAG, ".createUri starts")

//        var uri = Uri.parse(baseUrl) //Create the baseurl and then add the parameters on top of that
//        var builder = uri.buildUpon()
//        builder = builder.appendQueryParameter("tags", searchCriteria) //the appendQueryParameters function takes care of separating the parameters with a question mark or ampersand as appropriate and also makes sure the next step results in a valid uri.
//        builder = builder.appendQueryParameter("tagmode", if (matchAll) "ALL" else "ANY") //Each time we build we get back the baseurl with the newly added parameter added to the end
//        builder = builder.appendQueryParameter("lang", lang)
//        builder = builder.appendQueryParameter("format", "json")
//        builder = builder.appendQueryParameter("nojsoncallback", "1")
        //Detta kan skriva snyggare nedan.

        //Chaining function calls!
        return  Uri.parse(baseUrl)
            .buildUpon()
            .appendQueryParameter("tags", searchCriteria)
            .appendQueryParameter("tagmode", if (matchAll) "ALL" else "ANY")
            .appendQueryParameter("lang", lang).appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1").build().toString()

    }
//   Menu Method
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, "onCreateOptionsMenu called")
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
//  Menu Method, when you click an item
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected called")
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_search -> {
                startActivity(Intent(this, SearchActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //Companion Object är en Class variabel till skillnad från en Instance variable. Det finns bara en av den.
    //Fördelen med det är ifall jag har 1000 MainActivity() så har jag inte 1000 private val const TAG som äter upp allt minne.
    //Utan jag har bara en, därför companion object är en singleton!

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onDownloadComplete(data: String, status: DownloadStatus) {
        if (status == DownloadStatus.OK) {
            Log.d(TAG, "onDownloadComplete called")

            val getFlickrJsonData = GetFlickrJsonData(this)
            getFlickrJsonData.execute(data) //Kör doInBackground och kör sen onPostExecute
        } else {
            Log.d(TAG, "onDownloadComplete failed with status $status. Error message is: $data")
        }
    }

    override fun onDataAvailable(data: List<Photo>) {
        Log.d(TAG, ".onDataAvailable called, data is $data")

        //Här är det smidigt att göra en fun i recycler istället för att omdeklarera recyclern
        flickrRecyclerViewAdapter.loadNewData(data)
        Log.d(TAG, ".onDataAvailable ends")
    }

    override fun onError(exception: Exception) {
        Log.d(TAG, "onError called with ${exception.message}")
    }

    override fun onItemClick(view: View, position: Int) {
        Log.d(TAG, ".onItemClick: starts")
        Toast.makeText(this, "Normal tap at position $position", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "TJABBA")

    }

    override fun onItemLongClick(view: View, position: Int) {
        Log.d(TAG, ".onItemLongClick: starts")
     //   Toast.makeText(this, "Long tap at position $position", Toast.LENGTH_SHORT).show()
        val photo = flickrRecyclerViewAdapter.getPhoto(position)
        if (photo != null) {
            val intent = Intent(this, PhotoDetailsActivity::class.java) //Skriver ::class.java så jag kan skapa en class literal och skicka själva instancen av classen som en parameter

            //Jag måste göra photo Serializable! "To serialize an object means tro convert its state to a byte stream so that the byte stream can
            // be reverted back into a copy of the object". So if an object is serializable it can be stored and retrieved.
            //Now, a byte stream can be saved to disk or held in memory.
            intent.putExtra(PHOTO_TRANSFER, photo) //This is similar on Bundle when were saving the activity state when we're rotating the screen. So we need to provide a key and a value, then use the key to retrieve it.
            //Vi skapade ju en constant i BaseActivity classen så vi skulle vara säkra att vi inte skriver något annat i dem 3 andra activitisen.
            startActivity(intent)
        }
    }
}
