package com.example.flickrbrowser

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View

internal const val FLICKR_QUERY = "FLICKR_QUERY" //internal = ska bara finnas i denna modulen
internal const val PHOTO_TRANSFER = "PHOTO_TRANSFER"

//We dont want BaseActivity to be added in the manifest! Our app wont create instances of Base Activity, so theres no point in registering it.

//All thios class does is inflate the toolbar.xml file, then use the setSupportActionBar fun with the inflated toolbar,
//to put the toolbar in place at the top of the screen.
//Jag gör detta för att inte behöva upprepa setSupportActionBar() metoden i onCreate på andra activitys, för att spara kod.
@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {
    private val TAG = "BaseActivity"

   internal fun activateToolbar(enableHome: Boolean) {
       Log.d(TAG, ".activateToolbar")

       val toolbar = findViewById<View>(R.id.toolbar) as android.support.v7.widget.Toolbar
       setSupportActionBar(toolbar)
       supportActionBar?.setDisplayHomeAsUpEnabled(enableHome)//The action bar will automatically add the  home button if we tell it to.
   }
}