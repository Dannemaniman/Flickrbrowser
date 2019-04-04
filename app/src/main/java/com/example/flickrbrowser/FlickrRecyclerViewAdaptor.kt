package com.example.flickrbrowser

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

//Det enda ViewHoldern gör är att den sparar imageviewn och textviewn som kommer visa data sen,
//"its basically just a way of storing references to the widgets in the view thatll be displayed by the recyclerview."
//view.findViewById() är en kostsam operation, därför är detta bättre att konstant ha en referens till widgeten.
class FlickrImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var thumbnail: ImageView = view.findViewById(R.id.thumbnail)
    var title: TextView = view.findViewById(R.id.title)
}

class FlickrRecyclerViewAdaptor(private var photoList: List<Photo>) : RecyclerView.Adapter<FlickrImageViewHolder>() {
    private val TAG = "FlickrRecyclerViewAdapt" // Obs, max 23 bokstäver!!

    override fun getItemCount(): Int {
       // Log.d(TAG, ".getItemCount called")
        return if(photoList.isNotEmpty()) photoList.size else 1 //var 0 men satte den till 1 så att jag ska få upp min placeholder på rad 41 ifall jag inte hade några sökresultat.

    }

    fun loadNewData(newPhotos: List<Photo>) {
        photoList = newPhotos
        notifyDataSetChanged() //ladda om
    }

    fun getPhoto(position: Int): Photo? {
        return if(photoList.isNotEmpty()) photoList[position] else null
    }

    override fun onBindViewHolder(holder: FlickrImageViewHolder, position: Int) {
        //this methods called by the recycler view when it wants new data to be stored in an existing view
        if(photoList.isEmpty()) {
            holder.thumbnail.setImageResource(R.drawable.placeholder)
            holder.title.setText(R.string.empty_photo)
        } else {
            //Jag hade kunnat göra en AsyncTask och ladda ner bilden där, men istället så finns det ett bibliotek som heter Picasso, som inte bara laddar ner
            //bilden men den cachar även den, så den inte laddas ner igen!
            val photoItem = photoList[position] //retrieve correct item out of list
            // Log.d(TAG, "onBindViewHolder: ${photoItem.title} --> $position")

            //P.S! context = holder.thumbnail.context, fungerar därför att alla views existerar i en context, och alla har en getContext funktion
            //Picasso är en singleton och du kan chaina function calls här!
            Picasso.get().load(photoItem.image) //Här hämtar jag ner bilden
                .error(R.drawable.placeholder) //Ifall jag har error
                .placeholder(R.drawable.placeholder) //Sätter placeholder medans bilden laddas ner
                .into(holder.thumbnail) //sen sparar vi den i imageview widgeten
            //Allt detta gör Picasso i bakgrunden
            holder.title.text = photoItem.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlickrImageViewHolder {
        //Called by the layout manager when it needs a new view
        Log.d(TAG, ".onCreateViewHolder new view requested")
        //Were inflating the view from XML as previously discussed.
        val view = LayoutInflater.from(parent.context).inflate(R.layout.browse, parent, false) //attachRoot tells the inflater not to attach the inflated view to the parent layout
        return FlickrImageViewHolder(view)
        //This function is there to inflate a view from the browse dot XML layout we created, and then return that view.
    }

}

