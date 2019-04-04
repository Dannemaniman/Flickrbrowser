package com.example.flickrbrowser

import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.content_photo_details.*

//Lets get MainActivity to launch the PhotoDetailsActivity, when a photo is tapped in the list. So well get started by creating
//that base class, base activity, that our activities will extend. The reason for doing that is to share function and properties
//amongst a number of different activities (three in our case). By defining common methods in a singla class, and then having the
//other classes extend it, they get access to everything thats defined in the base class. So we wont have to include the same function in 3
//different classes.

class PhotoDetailsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_details)
        activateToolbar(true)

        //val photo = intent.getSerializableExtra(PHOTO_TRANSFER) as Photo //.getSerializableExtra i med att jag har Serializat objektet
        val photo = intent.extras.getParcelable<Photo>(PHOTO_TRANSFER) as Photo

        //photo_title.text = "Title: " + photo.title
        photo_title.text = resources.getString(R.string.photo_title_text, photo.title)
       // photo_tags.text = "Tags: " + photo.tags
        photo_tags.text = resources.getString(R.string.photo_tags_text, photo.tags)
        //resources is a property of activities and it lets us access the apps resources.
        //photo_author.text = resources.getString(R.string.photo_author_text, "My", "Red", "Car") //genom %2$s metoden så kan android skifta om orden åt mig beroende på vilket språk användaren har
        photo_author.text = photo.author
        Picasso.get().load(photo.link) //Här hämtar jag ner bilden
            .error(R.drawable.placeholder) //Ifall jag har error
            .placeholder(R.drawable.placeholder) //Sätter placeholder medans bilden laddas ner
            .into(photo_image)
    }

}
