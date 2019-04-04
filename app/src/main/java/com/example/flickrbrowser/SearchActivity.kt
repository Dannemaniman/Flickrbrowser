package com.example.flickrbrowser

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.widget.SearchView

//Lägger till saker i xml dokumentet!
//Lägger även till i Manifest filen
//The idea is that the activity thats started, deals with the query. It performs the search using the details entered by the user.
//But in fact were going to be doing things slightly differently, were going to respond to a callback from the search view widget and deal with the
//users query that way.
//Now unfortunately the getSearchable info function seems to need both the meta data and the intent-filter, else we get an error.
class SearchActivity : BaseActivity() {
    private val TAG = "SearchActivity"
    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, ".onCreate: Starts")
        setContentView(R.layout.activity_search)
        activateToolbar(true)
        Log.d(TAG, ".onCreate: Ends")
    }
    //inflate the menu_search inside this activity
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, ".onCreateOptionsMenu: Starts")
        menuInflater.inflate(R.menu.menu_search, menu)

        //Boiler plate code
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
        val searchableInfo = searchManager.getSearchableInfo(componentName)
        searchView?.setSearchableInfo(searchableInfo)

        searchView?.isIconified = false

        //Istället för att implementa SearchView.OnQueryTextListener i class definitionen, så implementar jag genom att använda anonymous classes, som i calculator appen.
        //This is a standard pattern when implementing listeners and the most common one to use when you want to apply the listener to a single object.
        searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d(TAG, ".onQueryTextSubmit: called")

                val sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext) //Using applicationcontext instead of this because data is going to be retrieved by a different activity that saved it.
                sharedPref.edit().putString(FLICKR_QUERY, query).apply() //.edit() = put it into a writeable state
                searchView?.clearFocus() //for external keyboard, when you press enter on keyboard so it doesnt relaunch SearchActivity
                finish() //finish closes the activity and returns to whichever activity launched it.
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        searchView?.setOnCloseListener {
            finish()
            false
        }

        Log.d(TAG, ".onCreateOptionsMenu: returning")
        return true
    }
    //When we submit the search, android looks for an activity in the manifest that has the action.search intent filter and launches that activity
    //Which in this case is our SearchActivity
}
